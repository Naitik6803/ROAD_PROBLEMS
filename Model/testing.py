import cv2
import numpy as np

net = cv2.dnn.readNet('yolov4-custom_last.weights', 'yolov4-custom.cfg')
classes=[
0,
1
    ]
classes = ['accident','car']


#cap = cv2.VideoCapture('video4.mp4')
#cap = 'test_images/<your_test_image>.jpg'+*
font = cv2.FONT_HERSHEY_PLAIN
colors = np.random.uniform(0, 255, size=(100, 3))

while True:
    #_, img = cap.read()
    img = cv2.imread("57.jpg")

    height, width, _ = img.shape
    print(height,width)
    blob = cv2.dnn.blobFromImage(img, 1/255, (608, 608), (0,0,0), swapRB=True, crop=False)
    net.setInput(blob)
    output_layers_names = net.getUnconnectedOutLayersNames()
    layerOutputs = net.forward(output_layers_names)

    boxes = []
    confidences = []
    class_ids = []

    for output in layerOutputs:
        for detection in output:
            print(detection)
            scores = detection[5:]
            print(scores)
            class_id = np.argmax(scores)
            confidence = scores[class_id]
            if confidence > 0.6 :
                center_x = int(detection[0]*width)
                print(center_x)
                center_y = int(detection[1]*height)
                print(center_y)
                w = int(detection[2]*width)
                print(detection[3])
                h = int(detection[3]*height)

                x = int(center_x - w/2)
                y = int(center_y - h/2)

                boxes.append([x, y, w, h])
                confidences.append((float(confidence)))
                class_ids.append(class_id)
                break
    indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.2, 0.4)
    print(indexes)
    if len(indexes)>0:
        for i in indexes.flatten():
            print(x,y,w,h)
            x, y, w, h = boxes[i]
            label = str(classes[class_ids[i]])
            confidence = str(round(confidences[i],2))
            color = colors[i]
            cv2.rectangle(img, (x,y), (x+w, y+h), color, 2)
            cv2.putText(img, label + " " + confidence, (x, y+20), font, 1, (255,255,255), 2)

    cv2.imshow('Image', img)
    key = cv2.waitKey(1)
    if key==27:
        break

#cap.release()
cv2.destroyAllWindows()

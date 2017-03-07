import cv2
from backGroundSub import BackGroundSubtraction
from plateSub import PlateSub
import imutils
import math

Debug = True


# img1 = cv2.imread("0_0.jpeg")
# img2 = cv2.imread("4_0.jpeg")
plate_img = cv2.imread("plate.png")
# img1 = imutils.resize(img1, height=480,width=640)
# img2 = imutils.resize(img2, height=480,width=640)
# plate_img = imutils.resize(plate_img, height=480,width=640)



# test = BackGroundSubtraction(Debug,img1)
# [result,position] = test.image_processing(img2)
# if Debug:
# 	cv2.imshow("BackGroundResult",result)
# 	cv2.waitKey(0)
# plate_test = PlateSub(Debug,plate_img)
# for (x,y,w,h) in position:
# 	if Debug:
# 		cv2.imshow("plate",img2[y:y+h,x:x+w])
# 		cv2.waitKey(0)
# 	if w < h:
# 		h = w
# 	elif w > h:
# 		w = h
# 	color_plate = plate_test.image_processing(img2[y:y+h,x:x+w])
# 	

camera = cv2.VideoCapture(0)
(grabbed,firstFrame) = camera.read()
firstFrame = imutils.resize(firstFrame,height=480,width=640)
test = BackGroundSubtraction(Debug,firstFrame)
plate_test = PlateSub(Debug,plate_img)

while True:
	(grabbed,frame) = camera.read()

	if not grabbed:
		break

	frame = imutils.resize(frame,height=480,width=640)

	[result, position] = test.image_processing(frame)
	cv2.imshow("BackGroundResult",result)
	for (x,y,w,h) in position:
		if w < h :
			h = w
		elif h < w:
			w = h
		color_plate = plate_test.image_processing(frame[y:(y+h),x:(x+w)])
		if color_plate:
			print color_plate

	key = cv2.waitKey(1) and 0xFF

	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()







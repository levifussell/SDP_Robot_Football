import datetime
import imutils
import time
import cv2
import numpy as np
from backgroundAveraging import BackGroundAveraging
from shapeDetector import ShapeDetector
from colourDetector import ColourDetector
from colourContain import ColourContain

class PlateSub:

	def __init__(self,DEBUG,backgroundFrame):
		self.backgroundFrame = backgroundFrame.copy()
		self.DEBUG = DEBUG


	def image_processing(self,currentFrame):
		np.set_printoptions(threshold=np.nan)
		currentFrame_copy = currentFrame.copy()
		# if currentFrame_copy.shape[0] < self.backgroundFrame.shape[0]:
		# 	height = currentFrame_copy.shape[0]

		# 	if currentFrame_copy.shape[1] < self.backgroundFrame.shape[1]:
		# 		width = currentFrame_copy.shape[1]
		# 	elif currentFrame_copy.shape[1] >= self.backgroundFrame.shape[1]:
		# 		width = self.backgroundFrame.shape[1]

		# elif currentFrame_copy.shape[0] >= self.backgroundFrame.shape[0]:
		# 	height = self.backgroundFrame.shape[0]

		# 	if currentFrame_copy.shape[1] < self.backgroundFrame.shape[1]:
		# 		width = currentFrame_copy.shape[1]
		# 	elif currentFrame_copy.shape[1] >= self.backgroundFrame.shape[1]:
		# 		width = self.backgroundFrame.shape[1]

		width = min(currentFrame_copy.shape[0], self.backgroundFrame.shape[0])
		height = min(currentFrame_copy.shape[1], self.backgroundFrame.shape[1])

		# currentFrame_copy = imutils.resize(currentFrame_copy, height=height,width=width)
		# self.backgroundFrame = imutils.resize(self.backgroundFrame, height=height,width=width)
		currentFrame_copy = np.resize(currentFrame_copy, (height,width,3))
		self.backgroundFrame = np.resize(self.backgroundFrame, (height,width,3))

		currentGray = cv2.cvtColor(currentFrame_copy,cv2.COLOR_BGR2GRAY)
		currentGray = cv2.GaussianBlur(currentGray,(1,1),0)

		backGroundGray = cv2.cvtColor(self.backgroundFrame,cv2.COLOR_BGR2GRAY)
		backGroundGray = cv2.GaussianBlur(backGroundGray,(1,1),0)

		if(self.DEBUG):
			cv2.imshow("CurrentGaussianBlur",currentGray)
			cv2.imshow("BackGroundGaussianBlur",backGroundGray)
			cv2.waitKey(0)

		print currentGray.shape
		print backGroundGray.shape
		cv2.accumulateWeighted(currentGray,backGroundGray.astype("float"),0.5)
		frameDelta = cv2.absdiff(cv2.convertScaleAbs(backGroundGray),currentGray)
		#frameDelta = cv2.absdiff(backGroundGray,currentGray)

		if(self.DEBUG):
			cv2.imshow("frameDelta",frameDelta)
			cv2.waitKey(0)

		#set the delta is less than 50, we discard the pixel and set it to black. 
		#If the delta is greater than 50, we will set it to white
		if(self.DEBUG):
			print frameDelta
		thresh = cv2.threshold(frameDelta,50,255,cv2.THRESH_BINARY)[1]
		if(self.DEBUG):
			cv2.imshow("thresh1",thresh)
			cv2.waitKey(0)

		thresh = cv2.dilate(thresh,None,iterations=1)
		if(self.DEBUG):
			cv2.imshow("thresh2",thresh)
			cv2.waitKey(0)

		filter_image = np.zeros((currentFrame_copy.shape),dtype="uint8")
		for i in range (0,thresh.shape[0]):
			for j in range (0,thresh.shape[1]):
				if thresh[i,j] == 255:
					filter_image[i,j] = currentFrame_copy[i,j].copy()

		if(self.DEBUG):
			cv2.imshow("filter_image",filter_image)
			cv2.waitKey(0)

		cp = ColourContain(self.DEBUG)
		color_plate = cp.colour_contain(filter_image)

		if(self.DEBUG):
			print "The plate contains "
			print color_plate

		return color_plate




		# [_,cnts,_] = cv2.findContours(thresh.copy(),cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

		# position = []
		# sd = ShapeDetector()
		# cl = ColourDetector()
		# for c in cnts:
		# 	#set minimum number of pixels that considers object
		# 	if cv2.contourArea(c) < 100:
		# 		if(self.DEBUG):
		# 			print"The area of contour is %d"%(cv2.contourArea(c))
		# 		continue

		# 	M = cv2.moments(c)
		# 	cX = int((M["m10"] / M["m00"]))
		# 	cY = int((M["m01"] / M["m00"]))
		# 	shape = sd.detect(c)
		# 	color = cl.colour_detect(currentLab,c)
		# 	c = c.astype("int")
		# 	cv2.drawContours(currentFrame_copy,[c],-1,(0,255,0),2)
		# 	cv2.putText(currentFrame_copy, color, (cX, cY),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
		# 	cv2.imshow("Object Detection",currentFrame_copy)
		# 	cv2.waitKey(0)
		# return currentFrame_copy,position
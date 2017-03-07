import datetime
import imutils
import time
import cv2
import numpy as np
from backgroundAveraging import BackGroundAveraging

class BackGroundSubtraction:

	def __init__(self,DEBUG,backgroundFrame):
		self.backgroundFrame = backgroundFrame.copy()
		self.DEBUG = DEBUG


	def image_processing(self,currentFrame):
		np.set_printoptions(threshold=np.nan)
		currentFrame_copy = currentFrame.copy()

		currentGray = cv2.cvtColor(currentFrame_copy,cv2.COLOR_BGR2GRAY)
		currentGray = cv2.GaussianBlur(currentGray,(11,11),0)

		backGroundGray = cv2.cvtColor(self.backgroundFrame,cv2.COLOR_BGR2GRAY)
		backGroundGray = cv2.GaussianBlur(backGroundGray,(11,11),0)

		if(self.DEBUG):
			cv2.imshow("CurrentGaussianBlur",currentGray)
			cv2.imshow("BackGroundGaussianBlur",backGroundGray)
			cv2.waitKey(0)

		cv2.accumulateWeighted(currentGray,backGroundGray.astype("float"),0.5)
		frameDelta = cv2.absdiff(cv2.convertScaleAbs(backGroundGray),currentGray)

		if(self.DEBUG):
			cv2.imshow("frameDelta",frameDelta)
			cv2.waitKey(0)

		#set the delta is less than 20, we discard the pixel and set it to black. 
		#If the delta is greater than 20, we will set it to white
		if(self.DEBUG):
			print frameDelta
		thresh = cv2.threshold(frameDelta,30,255,cv2.THRESH_BINARY)[1]
		if(self.DEBUG):
			cv2.imshow("thresh1",thresh)
			cv2.waitKey(0)

		thresh = cv2.dilate(thresh,None,iterations=4)
		if(self.DEBUG):
			cv2.imshow("thresh2",thresh)
			cv2.waitKey(0)
		[_,cnts,_] = cv2.findContours(thresh.copy(),cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

		position = []
		for c in cnts:
			#set minimum number of pixels that considers object
			if cv2.contourArea(c) < 100:
				if(self.DEBUG):
					print"The area of contour is %d"%(cv2.contourArea(c))
				continue
			(x,y,w,h) = cv2.boundingRect(c)
			cv2.rectangle(currentFrame_copy,(x,y),(x+w,y+h),(0,255,0),2)
			position.append((x,y,w,h))
		return currentFrame_copy,position
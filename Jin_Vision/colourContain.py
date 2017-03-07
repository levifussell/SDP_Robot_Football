import cv2
import sys
import numpy as np

class ColourContain:

	def __init__(self,Debug):
		self.DEBUG = Debug
		#adjust the range of lower bound and upper bound for each color
		yellowLower = (25,125,105)
		yellowUpper = (30,255,255)
		blueLower = (55,15,80)
		blueUpper = (130,255,255)
		greenLower = (35,90,90)
		greenUpper = (70,255,255)
		redLower = (0,30,40)
		redUpper = (10,255,255)
		self.boundaries = [(yellowLower,yellowUpper),(blueLower,blueUpper),(greenLower,greenUpper)
		,(redLower,redUpper)]
		self.colourName = ["yellow",'blue',"green","red"]

	def colour_contain(self,image):
		colour_plate = []
		index = 0
		for (lower,upper) in self.boundaries:
			hsv = cv2.cvtColor(image,cv2.COLOR_BGR2HSV)
			lower = np.array(lower,dtype = np.uint8)
			upper = np.array(upper,dtype = np.uint8)
			mask = cv2.inRange(hsv,lower,upper)
			if self.DEBUG:
				cv2.imshow(self.colourName[index],mask)
				cv2.waitKey(0)
			if np.any(mask) and np.count_nonzero(mask) > 20:
				colour_plate.append(self.colourName[index])
			index += 1
		return colour_plate
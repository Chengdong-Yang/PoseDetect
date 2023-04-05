/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.kotlin.shoudlerdetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.text.TextUtils
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.InferenceInfoGraphic
import com.google.mlkit.vision.demo.java.MyDataHelper
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Locale
import kotlin.math.atan2

/** Draw the detected pose in preview.  */
class ShoulderGraphic internal constructor(
  overlay: GraphicOverlay,
  private val pose: Pose,
  private val showInFrameLikelihood: Boolean
) :
  GraphicOverlay.Graphic(overlay) {
  private val leftPaint: Paint
  private val rightPaint: Paint
  private val whitePaint: Paint
  private val tipPaint: Paint
  override fun draw(canvas: Canvas) {
    val landmarks =
      pose.allPoseLandmarks
    if (landmarks.isEmpty()) {
      return
    }
    // Draw all the points
    for (landmark in landmarks) {
      drawPoint(canvas, landmark.position, whitePaint)
      if (showInFrameLikelihood) {
        canvas.drawText(
          String.format(Locale.US, "%.2f", landmark.inFrameLikelihood),
          translateX(landmark.position.x),
          translateY(landmark.position.y),
          whitePaint
        )
      }
    }
    val leftShoulder =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_SHOULDER)
    val rightShoulder =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_SHOULDER)
    val leftElbow =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_ELBOW)
    val rightElbow =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_ELBOW)
    val leftWrist =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_WRIST)
    val rightWrist =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_WRIST)
    val leftHip =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_HIP)
    val rightHip =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_HIP)
    val leftKnee =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_KNEE)
    val rightKnee =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_KNEE)
    val leftAnkle =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_ANKLE)
    val rightAnkle =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_ANKLE)
    val leftPinky =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_PINKY)
    val rightPinky =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_PINKY)
    val leftIndex =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_INDEX)
    val rightIndex =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_INDEX)
    val leftThumb =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_THUMB)
    val rightThumb =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_THUMB)
    val leftHeel =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_HEEL)
    val rightHeel =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_HEEL)
    val leftFootIndex =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_FOOT_INDEX)
    val rightFootIndex =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_FOOT_INDEX)
    val rightEar =
      pose.getPoseLandmark(PoseLandmark.Type.RIGHT_EAR)
    val leftEar =
      pose.getPoseLandmark(PoseLandmark.Type.LEFT_EAR)
    /////////////////////
    //Calculate whether the hand exceeds the shoulder
    val yRightHand = rightWrist!!.position.y - rightEar!!.position.y
    val yLeftHand = leftWrist!!.position.y - leftEar!!.position.y
    val wristyDistance = leftWrist.position.y - rightWrist.position.y
    //Calculate whether the distance between the shoulder and the foot is the same width
    val shoulderDistance = leftShoulder!!.position.x - rightShoulder!!.position.x
    val wristxDistance = leftWrist!!.position.x - rightWrist!!.position.x
    val ratio = wristxDistance/shoulderDistance
    //angle of point 24-26-28
    val angle_right_elbow = getAngle(rightWrist, rightElbow, rightShoulder)
    val angle_left_elbow = getAngle(leftWrist, leftElbow, rightShoulder)
    val angle24_26_28 = getAngle(rightHip, rightKnee, rightAnkle)

    if(((180-Math.abs(angle24_26_28)) > 5) && !isCount){
      reInitParams()
      lineOneText = "Please stand up straight"
    }else if(wristxDistance < 0 && !isCount) {
      reInitParams()
      lineOneText = "Please put your hands parallel to the ground"
    }else if(wristyDistance>0&& !isCount){
      reInitParams()
      lineOneText = "Please put your hands in a line"}
    else{
    val currentHeight =  (rightWrist.position.y + leftWrist.position.y)/2 //Judging up and down by wrist height
    if(!isCount&&yLeftHand>0&&yRightHand>0){
      wristHeight = currentHeight
      minSize = Math.abs(rightElbow!!.position.y - rightWrist!!.position.y)/3
      isCount = true
      lastHeight = currentHeight
      lineOneText = "Gesture ready"
    }
    if(!isUp && (lastHeight-currentHeight)>0&&yLeftHand<0&&yRightHand<0){//开始上举
      isUp = true
      isDown = false
      upCount++
      lastHeight = currentHeight
      lineTwoText = "start up"
    }else if((lastHeight-currentHeight)>0&&yLeftHand<=0&&yRightHand<=0){
      lineTwoText = "uping"
      lastHeight = currentHeight
    }
    if(!isDown && (downCount < upCount) && (lastHeight-currentHeight)<0&&yLeftHand>0&&yRightHand>0){//开始起身
      isDown = true
      isUp = false
      downCount++
      lastHeight = currentHeight
      lineTwoText = "start up"
    }else if((currentHeight - lastHeight) >0){
      lineTwoText = "downing"
      lastHeight = currentHeight
    }
  }

    //我想对手臂挥舞的次数进行计数，手腕手肘和肩膀的角度作为hen


    drawText(canvas, lineOneText,1)
    drawText(canvas, lineTwoText,2)
    drawText(canvas, "count："+upCount.toString(), 3)
    //我想将upCount存储到数据库中，但是我不知道如何将其存储到数据库中，请帮助我
// Draw all the points
    /////////////////////
    drawLine(canvas, leftShoulder!!.position, rightShoulder!!.position, whitePaint)
    drawLine(canvas, leftHip!!.position, rightHip!!.position, whitePaint)
    // Left body
    drawLine(canvas, leftShoulder.position, leftElbow!!.position, leftPaint)
    drawLine(canvas, leftElbow.position, leftWrist!!.position, leftPaint)
    drawLine(canvas, leftShoulder.position, leftHip.position, leftPaint)
    drawLine(canvas, leftHip.position, leftKnee!!.position, leftPaint)
    drawLine(canvas, leftKnee.position, leftAnkle!!.position, leftPaint)
    drawLine(canvas, leftWrist.position, leftThumb!!.position, leftPaint)
    drawLine(canvas, leftWrist.position, leftPinky!!.position, leftPaint)
    drawLine(canvas, leftWrist.position, leftIndex!!.position, leftPaint)
    drawLine(canvas, leftAnkle.position, leftHeel!!.position, leftPaint)
    drawLine(canvas, leftHeel.position, leftFootIndex!!.position, leftPaint)
    // Right body
    drawLine(canvas, rightShoulder.position, rightElbow!!.position, rightPaint)
    drawLine(canvas, rightElbow.position, rightWrist!!.position, rightPaint)
    drawLine(canvas, rightShoulder.position, rightHip.position, rightPaint)
    drawLine(canvas, rightHip.position, rightKnee!!.position, rightPaint)
    drawLine(canvas, rightKnee.position, rightAnkle!!.position, rightPaint)
    drawLine(canvas, rightWrist.position, rightThumb!!.position, rightPaint)
    drawLine(canvas, rightWrist.position, rightPinky!!.position, rightPaint)
    drawLine(canvas, rightWrist.position, rightIndex!!.position, rightPaint)
    drawLine(canvas, rightAnkle.position, rightHeel!!.position, rightPaint)
    drawLine(canvas, rightHeel.position, rightFootIndex!!.position, rightPaint)
  }

  fun reInitParams(){
    lineOneText = ""
    lineTwoText = ""
    wristHeight = 0f
    minSize = 0f
    isCount = false
    isUp = false
    isDown = false
    upCount = 0
    downCount = 0
  }

  fun drawPoint(canvas: Canvas, point: PointF?, paint: Paint?) {
    if (point == null) {
      return
    }
    canvas.drawCircle(
      translateX(point.x),
      translateY(point.y),
      DOT_RADIUS,
      paint!!
    )
  }

  fun drawLine(
    canvas: Canvas,
    start: PointF?,
    end: PointF?,
    paint: Paint?
  ) {
    if (start == null || end == null) {
      return
    }
    canvas.drawLine(
      translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint!!
    )
  }

  fun drawText(canvas: Canvas, text:String, line:Int) {
    if (TextUtils.isEmpty(text)) {
      return
    }
    canvas.drawText(text, InferenceInfoGraphic.TEXT_SIZE*0.5f, InferenceInfoGraphic.TEXT_SIZE*3 + InferenceInfoGraphic.TEXT_SIZE*line, tipPaint)
  }

  companion object {
    private const val DOT_RADIUS = 8.0f
    private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f

    var isUp = false //是否起身
    var isDown = false //是否下蹲
    var upCount = 0 //up times
    var downCount = 0 //down times
    var isCount = false //is counting
    var lineOneText = ""
    var lineTwoText = ""
    var wristHeight = 0f //
    var minSize = 0f //最小移动单位，避免测算抖动出现误差
    var lastHeight = 0f
  }

  init {
    whitePaint = Paint()
    whitePaint.color = Color.WHITE
    whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
    leftPaint = Paint()
    leftPaint.color = Color.GREEN
    rightPaint = Paint()
    rightPaint.color = Color.YELLOW

    tipPaint = Paint()
    tipPaint.color = Color.WHITE
    tipPaint.textSize = 40f
  }

  fun getAngle(firstPoint: PoseLandmark?, midPoint: PoseLandmark?, lastPoint: PoseLandmark?): Double {
    var result = Math.toDegrees(atan2(1.0*lastPoint!!.getPosition().y - midPoint!!.getPosition().y,
            1.0*lastPoint.getPosition().x - midPoint.getPosition().x)
            - atan2(firstPoint!!.getPosition().y - midPoint.getPosition().y,
            firstPoint.getPosition().x - midPoint.getPosition().x))
    result = Math.abs(result) // Angle should never be negative
    if (result > 180) {
      result = 360.0 - result // Always get the acute representation of the angle
    }
    return result
  }
}

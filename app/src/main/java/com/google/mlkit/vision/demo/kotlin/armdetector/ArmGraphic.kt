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

package com.google.mlkit.vision.demo.kotlin.armdetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.text.TextUtils
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.InferenceInfoGraphic
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

/** Draw the detected pose in preview.  */
class ArmGraphic internal constructor(
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

 // i want to measure the angle of the arm,当我挥舞胳膊的时候，请在屏幕上显示胳膊肘的角度和挥舞的次数
    var lineOneText = ""  //第一行显示的文字
    var lineTwoText = ""  //第二行显示的文字
    var Count = 0  //挥舞的次数
    var angle = 0.0  //角度

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

    //如果右肘为空或者右肩为空或者右手腕为空，提示会说将手机摆正确
    if (rightElbow == null || rightShoulder == null || rightWrist == null) {
      lineOneText = "please put your phone correctly"
    }

    //如果左肘为空或者左肩为空或者左手腕为空，提示会说将手机摆正确
    if (leftElbow == null || leftShoulder == null || leftWrist == null) {
      lineTwoText = "please put your phone correctly"
    }
    if (leftElbow != null && leftShoulder != null && leftWrist != null) {
      angle = getAngle(leftShoulder, leftElbow, leftWrist)
      angle= (angle * 100).roundToInt() / 100.0
      lineOneText = "left elbow angle："+ angle.toString()
    }
    if (rightElbow != null && rightShoulder != null && rightWrist != null) {
      angle = getAngle(rightShoulder, rightElbow, rightWrist)
      // q:对angle的小数点后两位进行四舍五入
      angle= (angle * 100).roundToInt() / 100.0
      lineTwoText = "right elbow angle："+ angle.toString()
    }
    //当任意一个手肘角度从90到120度时，再从120到90度时。挥舞次数加一，统计挥舞的次数
    if (leftElbow != null && leftShoulder != null && leftWrist != null) {
      if (getAngle(leftShoulder, leftElbow, leftWrist) in 90.0..120.0) {
        if (getAngle(leftShoulder, leftElbow, leftWrist) in 120.0..90.0) {
          Count++
        }
      }
    }


    drawText(canvas, lineOneText,1)
    drawText(canvas, lineTwoText,2)
    drawText(canvas, "count："+ Count.toString(), 3)
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
    Count = 0
    prevDistance= null
//    shoulderHeight = 0f
//    minSize = 0f
//    isCount = false
//    isUp = false
//    isDown = false
//    upCount = 0
//    downCount = 0
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
    var lineOneText = ""
    var lineTwoText = ""
    var Count = 0
    var prevDistance: Float? = null
    private const val THRESHOLD_DISTANCE = 0.3f // 30cm
    private fun calculateDistance(p1: PointF, p2: PointF): Float {
      return hypot(p1.x - p2.x, p1.y - p2.y)
    }
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

}

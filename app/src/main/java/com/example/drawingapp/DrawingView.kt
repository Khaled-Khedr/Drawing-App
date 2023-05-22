package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.method.Touch
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(
    context, attrs
) //in order to let the android system recognize its a view not just a class u need to inherit from the view class
//the view class needs a context and an attribute set for its parameters
//this also allows us to use the Drawing View in our xml
{
    //drawing path
    private lateinit var drawpath: FingerPath

    //defines what to draw
    private lateinit var CanvasPaint: Paint

    //defines how to draw
    private lateinit var drawPaint: Paint
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private lateinit var canvasBitmap: Bitmap //bitmap holds the pixels together
    private var brushSize: Float = 0.toFloat()
    private val paths = mutableListOf<FingerPath>()

    //canvas defines shapes aka rectangle you can put on the screen
    //paint defines whatever colour/style/font u fill that rectangle with
    init {
        setUpDrawing()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas =
            Canvas(canvasBitmap)//an instance of the canvas class that provides functions to draw on a bitmap
    }

    //this function will be called by the system when the user is going to touch the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x //when user touches the x axis
        val touchY = event?.y //when user touches the y axis

        when (event?.action) {
            //this event will be fired when the user put the finger on the screen
            MotionEvent.ACTION_DOWN -> {
                drawpath.color = color
                drawpath.brushThickness = brushSize.toFloat()


                drawpath.reset() //resetting path before we set initial point
                drawpath.moveTo(touchX!!, touchY!!) //needs a null check
            }

            //this event will be fired when the user starts to ove his finger
            //this will be fired continuously until user picks up his finger

            MotionEvent.ACTION_MOVE -> {

                drawpath.lineTo(touchX!!, touchY!!)
            }

            //this event will be fired when the user will pick up the finger from screen

            MotionEvent.ACTION_UP -> {
                drawpath = FingerPath(color, brushSize)
                paths.add(drawpath) //add the drawpath to our mutable list
            }

            else -> return false

        }

        invalidate() //refreshing the layout to reflect the drawing changes
        return true

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmap, 0f, 0f, drawPaint)

        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas?.drawPath(path, drawPaint)
        }
        if (!drawpath.isEmpty) { //if the drawpath isnt empty then set the strokewidth to the brush thickness
            drawPaint.strokeWidth = drawpath.brushThickness
            drawPaint.color = drawpath.color //setting the colour
            canvas?.drawPath(drawpath, drawPaint)  //drawing path on the canvas
        }
    }

    private fun setUpDrawing() {
        drawPaint = Paint() //instance of the paint class used to define the style of the drawing
        drawpath = FingerPath(color, brushSize)
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE  //stroke style only a line not a field shape
        drawPaint.strokeJoin = Paint.Join.ROUND //making the ends are rounded
        drawPaint.strokeCap = Paint.Cap.ROUND

        CanvasPaint = Paint(Paint.DITHER_FLAG) //to smooth the colours being drawn
        brushSize = 20.toFloat()


    }

    fun changeBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        ) //this is because of varyin
        drawPaint.strokeWidth = brushSize //the stroke width changes according to brush size

    }

    fun setColor(newColor: Any) {  //newcolor is of any type
        if (newColor is String) {  //if new color is a string
            color = Color.parseColor(newColor)
            drawPaint.color = color//parseColor
        }else {
            color=newColor as Int
            drawPaint.color=color
        }
    }

    fun undoPath() {
        if (paths.size > 0) { //if the mutable list size is larger than 0
            paths.removeAt(paths.size - 1) //remove the last element of the finger path   list
            invalidate() //refreshing the layout to reflect the drawing changes
        }
    }

    internal inner class FingerPath(var color: Int, var brushThickness: Float) :
        Path() //inner class inheriting from the path class
    {
        //internal means only accessed in this module

    }


}

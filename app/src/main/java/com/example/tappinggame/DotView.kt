import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class DotView(context: Context) : View(context) {
    private var dotX: Float = 0f
    private var dotY: Float = 0f
    private var dotSize: Int = 0
    private var paint = Paint()
    private var isTapped = false
    private var isDisabled = false

    init {
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(dotX + dotSize / 2f, dotY + dotSize / 2f, dotSize / 2f, paint)
    }

    fun setPosition(x: Float, y: Float) {
        dotX = x
        dotY = y
        invalidate()
    }

    fun setSize(size: Int) {
        dotSize = size
        invalidate()
    }

    fun setColor(color: Int, alpha: Int = 255) {
        paint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        invalidate()
    }

    fun setTapped(tapped: Boolean) {
        isTapped = tapped
    }

    fun isTapped(): Boolean = isTapped

    fun setDisabled(disabled: Boolean) {
        isDisabled = disabled
    }

    fun isDisabled(): Boolean = isDisabled

    fun getColor(): Int = paint.color

    fun isPointInside(x: Float, y: Float): Boolean {
        val distanceX = Math.abs(x - (dotX + dotSize / 2f))
        val distanceY = Math.abs(y - (dotY + dotSize / 2f))
        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())
        return distance <= dotSize / 2
    }
}

package org.semprebon.droiddice3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @Mock
    var mMockContext: Context? = null

    @Mock var canvas: Canvas? = null
    @Mock var paint: Paint? = null

    @Test
    @Throws(Exception::class)
    fun mocksCanvas() {
        canvas = mock(Canvas::class.java)
        paint = mock(Paint::class.java)
        canvas?.drawRect(0.0f, 0.0f, 5.0f, 0.0f, paint)
        verify(canvas)?.drawRect(0.0f, 0.0f, 5.0f, 0.0f, paint)
    }


}
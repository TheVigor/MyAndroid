package com.noble.activity.myandroid.fragments

import android.app.ActivityManager
import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.noble.activity.myandroid.MainActivity
import com.noble.activity.myandroid.R
import com.noble.activity.myandroid.adapters.ItemAdapter
import com.noble.activity.myandroid.constants.GRAPHICS_INDEX
import com.noble.activity.myandroid.models.ItemInfo
import kotlinx.android.synthetic.main.fragment_graphics.*
import kotlinx.android.synthetic.main.toolbar_ui.*
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GraphicsFragment : Fragment() {

    private var graphicsInfoList: ArrayList<ItemInfo>? = null
    private var adapter: ItemAdapter? = null

    private var myGlSurfaceView: GLSurfaceView? = null

    val resolution: String
        get() {
            val wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            wm.defaultDisplay.getRealSize(size)
            return size.x.toString() + "x" + size.y
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_graphics, container, false)
        (activity as MainActivity).setAdapterPosition(GRAPHICS_INDEX)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graphicsInfoList = ArrayList()

        adapter = ItemAdapter((activity as MainActivity?)!!, graphicsInfoList!!)
        rvGraphicsData!!.setHasFixedSize(true)
        rvGraphicsData!!.layoutManager = LinearLayoutManager(activity!!)
        loadGPUData()

    }

    private fun initToolbar() {
        tv_title.text = activity!!.resources.getString(R.string.graphics)
        tv_title.setTextColor(ContextCompat.getColor(activity!!, R.color.gpu_ram))
    }

    private fun loadGPUData() {
        val activityManager = activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager
            .deviceConfigurationInfo
        if (!resolution.isEmpty())
            graphicsInfoList!!.add(ItemInfo("Resolution", resolution))

        val supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            myGlSurfaceView = GLSurfaceView(activity!!)
            framelayout!!.addView(myGlSurfaceView)
            myGlSurfaceView!!.setEGLContextClientVersion(2)

            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

            // Set the renderer to our demo renderer, defined below.
            val myRenderer = MyRenderer()
            myGlSurfaceView!!.setRenderer(myRenderer)
            myGlSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        } else {
            llEmptyStateGraphics!!.visibility = View.VISIBLE
            cvGraphicsDataParent!!.visibility = View.GONE
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
        }
    }

    private inner class MyRenderer internal constructor() : GLSurfaceView.Renderer {

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

            graphicsInfoList!!.add(
                ItemInfo("Graphics Device Name", "" + gl.glGetString(GL10.GL_RENDERER)))

            graphicsInfoList!!.add(
                ItemInfo("Graphics Device Vendor", "" + gl.glGetString(GL10.GL_VENDOR)))

            graphicsInfoList!!.add(
                ItemInfo("Graphics Device Version", "" + gl.glGetString(GL10.GL_VERSION)))

            graphicsInfoList!!.add(
                ItemInfo("Graphics Device Extensions", "" + gl.glGetString(GL10.GL_EXTENSIONS)))

            val maxSize = IntArray(1)
            gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSize, 0)

            graphicsInfoList!!.add(ItemInfo("Max Texture  SIze", "" + maxSize[0]))

            activity!!.runOnUiThread {
                llEmptyStateGraphics!!.visibility = View.GONE
                cvGraphicsDataParent!!.visibility = View.VISIBLE

                framelayout.removeView(myGlSurfaceView)
                rvGraphicsData!!.adapter = adapter
            }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        }

        override fun onDrawFrame(gl: GL10) {

        }
    }

}
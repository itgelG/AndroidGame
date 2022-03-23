package com.android.cy.androidmazegame.GameView;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.android.cy.androidmazegame.Objects.BasicObject;
import com.android.cy.androidmazegame.Objects.Cube;
import com.android.cy.androidmazegame.Objects.Plane;
import com.android.cy.androidmazegame.Scene.CharacterController;
import com.android.cy.androidmazegame.Scene.SceneManager;
import com.android.cy.androidmazegame.Utils.Vector3D;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ItgelG on 2022/2/24.
 */
public class GameRenderer implements GLSurfaceView.Renderer{

    private BasicObject object;
    private BasicObject plane;
    private BasicObject wall;
    private BasicObject roof;

    /** CharacterController */
    private CharacterController characterController;

    /** Проекцийн матрицыг хадгалах. Энэ нь дүр зургийг 2D харах талбарт тусгахад хэрэглэгддэг */
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mOrthoProjectionMatrix = new float[16];

    /**
     * Харах матрицыг хадгалах. Үүнийг манай камер гэж ойлгож болно. Энэхүү матриц нь дэлхийн орон зайг нүдний орон зай болгон хувиргадаг;
     * Энэ нь бидний нүдтэй харьцуулахад аливаа зүйлийг байрлуулдаг
     */
    private float[] mViewMatrix;
    /**
     * Загварын матрицыг хадгалах.
     * Энэ матриц нь загваруудыг объектын орон зайгаас (загвар бүрийг орчлон ертөнцийн төвд байрладаг гэж үзэж болно)
     * дэлхийн орон зай руу шилжүүлэхэд ашиглагддаг.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Загварын матрицын хуулбарыг гэрлийн байрлалд тусгайлан хадгалдаг.
     */
    private float[] mLightModelMatrix = new float[16];

    /** Загварын орон зайд гарал үүсэл дээр төвлөрсөн гэрлийг барихад ашигладаг.
     * Бидэнд 4-р координат хэрэгтэй тул үүнийг хувиргах матрицуудаараа үржүүлэхэд орчуулга хийх боломжтой болно. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    /** Дэлхийн орон зай дахь гэрлийн одоогийн байрлалыг барихад ашигладаг (загвар матрицаар хувиргасны дараа). */
    private final float[] mLightPosInWorldSpace = new float[4];

    /** Нүдний орон зайд гэрлийн хувирсан байрлалыг барихад ашигладаг (загвар харах матрицаар хувиргасны дараа) */
    private final float[] mLightPosInEyeSpace = new float[4];

    /** Context */
    private final Context mContextHandle;

    /** SceneManger */
    private SceneManager sceneManager;

    /** Render time */
    private float startTime = 0.f;

    private GameSurfaceView mGameSurfaceView;
    private int mGameState = -1;
    private final static int GAME_START = 0;


    public GameRenderer(Context context, GameSurfaceView g) { mContextHandle = context; mGameSurfaceView = g;}

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Арын дэвсгэр тод өнгийг саарал болгох.
        GLES20.glClearColor(0.8f, 0.5f, 0.5f, 1.0f);

        // Арын нүүрийг арилгахын тулд таслах аргыг ашиглана.
        // GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Culling back
        GLES20.glCullFace(GLES20.GL_BACK);

        // Front face
        GLES20.glFrontFace(GLES20.GL_CCW);

        // Create scene
        object = new Cube(mContextHandle);
        plane = new Plane(mContextHandle, 60.f, 60.f);
        roof = new Plane(mContextHandle, 60.f, 60.f);

        sceneManager = new SceneManager(mContextHandle);
        // Shal
        sceneManager.addObject(plane);
        plane.setPosition(new Vector3D(0.0f, -5.0f, 0.0f));
        // Taaz
        sceneManager.addObject(roof);
        roof.setPosition(new Vector3D(0.0f, 5.0f, 0.0f));

        // Create character controller
        characterController = new CharacterController(sceneManager);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // OpenGL харах цонхыг гадаргуутай ижил хэмжээтэй болгож тохируулах
        GLES20.glViewport(0, 0, width, height);

        // Шинэ хэтийн төлөвийн проекцын матриц үүсгэх. Өндөр нь ижил хэвээр байх болно
        // харин өргөн нь харьцаанаас хамаарч өөр өөр байх болно.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 866.0f;

        Log.v("GameRenderer", width + " " + height + " ratio" + ratio);
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, bottom, top, near, far);
        Matrix.orthoM(mOrthoProjectionMatrix, 0, 0f, width, 0.0f, height, 0, 50);
        // scenemanager-д зориулсан проекцийн матрицыг тохируулах
        sceneManager.setProjectionMatrix(mProjectionMatrix);
        sceneManager.setGamePadProjectionMatrix(mOrthoProjectionMatrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Game state: START
        if (mGameState != GAME_START) {
            mGameState = GAME_START;
            mGameSurfaceView.gameStart();
        }

        float elapsedTime = SystemClock.elapsedRealtime() - startTime;
        if (elapsedTime < 8.3f) {
            //     return;
        }
        startTime = SystemClock.elapsedRealtime();

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Update camera position
        characterController.update(1/60f);
        mViewMatrix = characterController.getViewMatrix();

        // Гэрлийн байрлалыг тооцоол. Эргүүлж, дараа нь зай руу түлхэнэ.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);

        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        sceneManager.setLightPosInEyeSpace(mLightPosInEyeSpace);
        sceneManager.render();
    }

    private float theta = 0, phi = 0;

    public void updateCamera(float x, float y) {
        theta += x / 300;
        phi += y / 300;

        Vector3D target = new Vector3D();
        target.x = (float) (Math.cos(theta) * Math.sin(phi));
        target.y = (float) Math.cos(-phi);
        target.z = (float) (Math.sin(theta) * Math.sin(phi));

        characterController.updateTarget(target);
        mViewMatrix = characterController.getViewMatrix();
    }

    public void onKeyDown(int direction) {
        characterController.onKeyDown(direction);
        mViewMatrix = characterController.getViewMatrix();
    }

    public void onKeyUp() {
        characterController.onKeyUp();
    }

    public static int loadShader(int type, String shaderCode){

        // оройн шэйдерийн төрлийг үүсгэх (GLES20.GL_VERTEX_SHADER)
        // эсвэл фрагмент шэйдерийн төрөл (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // эх кодыг шэйдерт нэмээд эмхэтгэ
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
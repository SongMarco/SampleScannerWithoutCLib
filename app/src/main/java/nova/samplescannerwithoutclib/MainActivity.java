package nova.samplescannerwithoutclib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;



/*
   이미지 스캔 구현 예제

   1) 개요

   이 기능은 문자 인식 기능을 보조하기 위한 기능이다.
   문자 인식을 할 때, 글씨가 기울어지지 않은 반듯한 이미지가 인식이 잘 되기 때문이다.

   사진을 촬영하거나, 이미지를 갤러리에서 가져왔을 때,
   기울어진 이미지를 스캔한 것처럼 반듯하게 만들어준다.

   2) 관련 기술

   이 기능에는 openCV의 perspective transformation 이라는 기능이 적용됐다.

   원하는 영역을 지정하여, 기울어지거나 삐뚤어진 부분을 반듯하게 만들어준다.
   아래의 예제와 이미지를 보면 쉽게 알 수 있다.
   http://miatistory.tistory.com/5




  */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;
    private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private ImageView scannedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        // 이 버튼은 아무 기능을 하지 않는다.(더미 데이터)
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());


        // 카메라 버튼 :
        // 사진을 촬영하고,
        // 사진을 반듯하게 만드는 기능이 적용되어 이미지뷰에 세팅된다.
        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));


        // 갤러리 버튼
        // 갤러리에서 사진을 가져와 반듯하게 만들고, 이미지뷰에 세팅한다.
        mediaButton = (Button) findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));


        // 반듯하게 만든 이미지가 세팅되는 이미지뷰
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
    }

    // 버튼에 대한 클릭리스너
    private class ScanButtonClickListener implements View.OnClickListener {

        //설정 : 카메라인가, 갤러리인가를 결정
        private int preference;

        //리스너의 생성자. 설정을 파라미터로 넣어 카메라 / 갤러리 버튼을 구분하도록 함
        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        //더미데이터의 생성자. 아무 설정이 없음.
        public ScanButtonClickListener() {
        }


        // 버튼을 누르면 설정에 따라 스캔 기능이 시작됨.
        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    //스캔을 시작하는 메소드
    protected void startScan(int preference) {

        // 메인 액티비티에서, 스캔 라이브러리 액티비티로 이동한다.
        // 스캔 라이브러리 액티비티에서 사진 촬영, 갤러리에서 사진 가져오기, 사진 변형이 이루어진다.
        Intent intent = new Intent(this, ScanActivity.class);

        // preference 설정에 따라 다르게 행동한다.(카메라 / 갤러리)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);

        startActivityForResult(intent, REQUEST_CODE);
    }


    // 스캔 라이브러리 액티비티에서 돌아왔다.
    // 반듯해진 사진을 이미지 뷰에 세팅하여 작업을 마치게 된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 리퀘스트 코드, 액티비티에서 반환한 결과값이 정상이면 이미지뷰 세팅
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;

            //이미지뷰에 세팅
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);

                scannedImageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


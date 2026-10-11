package com.aizora.aicamera;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.PixelCopy;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
    private ExoPlayer player;
    private PlayerView video;
    private EditText address;
    private TextView status;
    private LinearLayout controls;
    private boolean fullscreen = false;
    private static final String DEFAULT_URL = "rtsp://192.168.0.102:554/live/ch00_1";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(12,19,38));
        getWindow().setNavigationBarColor(Color.rgb(12,19,38));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(14),dp(18),dp(14),dp(12));
        page.setBackgroundColor(Color.rgb(12,19,38));
        TextView title = new TextView(this);
        title.setText("◉  AIZORA AI Camera"); title.setTextSize(23); title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        page.addView(title);
        TextView sub = new TextView(this);
        sub.setText("V1.1  •  Local Wi-Fi RTSP viewer"); sub.setTextColor(Color.rgb(158,177,208));
        page.addView(sub);
        video = new PlayerView(this);
        video.setUseController(true);
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(-1,0,1);
        vp.topMargin=dp(18); vp.bottomMargin=dp(14);
        page.addView(video,vp);
        controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        address = new EditText(this);
        address.setSingleLine(true); address.setTextColor(Color.WHITE);
        address.setHintTextColor(Color.GRAY); address.setHint("rtsp://camera-ip:554/path");
        address.setText(getPreferences(0).getString("url",DEFAULT_URL));
        controls.addView(address,new LinearLayout.LayoutParams(-1,dp(55)));
        LinearLayout row1 = new LinearLayout(this);
        row1.addView(button("▶ Connect", () -> connect()), new LinearLayout.LayoutParams(0,dp(48),1));
        row1.addView(button("■ Stop", () -> stop()), new LinearLayout.LayoutParams(0,dp(48),1));
        controls.addView(row1);
        LinearLayout row2 = new LinearLayout(this);
        row2.addView(button("📷 Snapshot", () -> snapshot()),new LinearLayout.LayoutParams(0,dp(48),1));
        row2.addView(button("⛶ Fullscreen", () -> toggleFullscreen()),new LinearLayout.LayoutParams(0,dp(48),1));
        controls.addView(row2);
        status = new TextView(this); status.setText("Ready — connect to the same Wi-Fi as camera");
        status.setTextColor(Color.rgb(160,201,255)); status.setPadding(0,dp(12),0,dp(4));
        controls.addView(status);
        page.addView(controls);
        setContentView(page);
    }
    private int dp(int x) {return (int)(getResources().getDisplayMetrics().density*x+0.5f);}
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false);
        b.setTextColor(Color.WHITE); b.setTextSize(13);
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xff2765dc,0xff14a5cb});
        bg.setCornerRadius(dp(12)); b.setBackground(bg);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-1);
        p.setMargins(dp(3),dp(3),dp(3),dp(3)); b.setOnClickListener(v->action.run()); return b;
    }
    private void connect() {
        String url=address.getText().toString().trim();
        if(!url.startsWith("rtsp://") && !url.startsWith("rtsps://")) {status.setText("Enter a valid RTSP URL"); return;}
        getPreferences(0).edit().putString("url",url).apply(); stop();
        player = new ExoPlayer.Builder(this).build(); video.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override public void onPlaybackStateChanged(int state) {
                if(state==Player.STATE_READY) status.setText("● Live video connected");
                else if(state==Player.STATE_BUFFERING) status.setText("Connecting to camera…");
            }
            @Override public void onPlayerError(androidx.media3.common.PlaybackException error) {
                status.setText("Connection error: "+error.getErrorCodeName());
            }
        });
        player.setMediaItem(MediaItem.fromUri(url)); player.prepare(); player.play();
    }
    private void stop() {if(player!=null){player.release();player=null;video.setPlayer(null);} if(status!=null)status.setText("Disconnected");}
    private void snapshot() {
        View surface=video.getVideoSurfaceView();
        if(!(surface instanceof SurfaceView) || player==null || player.getPlaybackState()!=Player.STATE_READY) {
            Toast.makeText(this,"Connect camera before snapshot",Toast.LENGTH_SHORT).show(); return;
        }
        SurfaceView sv=(SurfaceView)surface;
        Bitmap bitmap=Bitmap.createBitmap(Math.max(1,sv.getWidth()),Math.max(1,sv.getHeight()),Bitmap.Config.ARGB_8888);
        PixelCopy.request(sv,bitmap,result->{
            if(result!=PixelCopy.SUCCESS){bitmap.recycle();Toast.makeText(this,"Snapshot failed: "+result,Toast.LENGTH_SHORT).show();return;}
            File dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if(dir==null){bitmap.recycle();Toast.makeText(this,"Storage unavailable",Toast.LENGTH_SHORT).show();return;}
            File file=new File(dir,"AIZORA_"+System.currentTimeMillis()+".png");
            try(FileOutputStream out=new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                Toast.makeText(this,"Saved: "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
            }catch(Exception e){Toast.makeText(this,"Save failed: "+e.getMessage(),Toast.LENGTH_LONG).show();}
            finally {bitmap.recycle();}
        },new Handler(Looper.getMainLooper()));
    }
    private void toggleFullscreen() {
        fullscreen=!fullscreen;
        controls.setVisibility(fullscreen?View.GONE:View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(fullscreen?(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY):0);
        setRequestedOrientation(fullscreen?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if(fullscreen) Toast.makeText(this,"Press Back to exit fullscreen",Toast.LENGTH_SHORT).show();
    }
    @Override public void onBackPressed(){if(fullscreen){toggleFullscreen();}else super.onBackPressed();}
    @Override protected void onDestroy(){stop();super.onDestroy();}
}

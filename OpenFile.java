package fr.product.greg.pleiadmini;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by greg on 28/07/15.
 */
public class OpenFile extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         String url=getIntent().getStringExtra("url");
        openURLWithType(url);

    }
    public boolean openURLWithType( String url  ) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent( Intent.ACTION_VIEW, uri );
        intent.setDataAndType( uri, "application/pdf" );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        try {
            OpenFile.this.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {

        }
        return false;
    }


}

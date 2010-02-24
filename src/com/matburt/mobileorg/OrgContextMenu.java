package com.matburt.mobileorg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.view.View;
import android.view.View.OnClickListener;

public class OrgContextMenu extends Activity implements OnClickListener
{
    public static final String LT = "MobileOrg";
    ArrayList<Integer> npath;
    private Button docButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.longcontext);
        this.docButton = (Button)this.findViewById(R.id.documentMode);
        this.docButton.setOnClickListener(this);
        this.poplateDisplay();
    }

    public void poplateDisplay() {
        Intent txtIntent = getIntent();
        this.npath = txtIntent.getIntegerArrayListExtra("nodePath");
    }

    public void onClick(View v) {
        MobileOrgApplication appInst = (MobileOrgApplication)this.getApplication();
        Node thisNode = appInst.rootNode;
        Intent textIntent = new Intent();
        String displayBuffer = new String();
        for (int idx = 0; idx < this.npath.size(); idx++) {
            thisNode = thisNode.subNodes.get(
                                             this.npath.get(idx));
        }
        for (int idx = 0; idx < thisNode.subNodes.size(); idx++) {
            displayBuffer += thisNode.subNodes.get(idx).nodeName +
                "\n" + thisNode.subNodes.get(idx).nodePayload + "\n\n";
        }

        textIntent.setClassName("com.matburt.mobileorg",
                                "com.matburt.mobileorg.SimpleTextDisplay");
        textIntent.putExtra("txtValue", displayBuffer);
        startActivity(textIntent);
    }
}
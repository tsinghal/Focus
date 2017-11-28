package dreamteam.focus.client;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.events.DriveEventService;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import dreamteam.focus.R;
/**
 * PARTS OF THE CODE ARE TAKEN AS IT IS FROM A SITE AND NOT WRITTEN ON OUR OWN
 * SOURCE:
 * https://www.numetriclabz.com/integrate-google-drive-in-android-tutorial/
 * CODE USED FROM THAT SITE^
 */
public class GoogleAuthentication extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Google Drive Activity";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    private DriveId mFileId;
    public DriveFile file;
    private Button uploadButton;
    private Button downloadButton;
    private File i;
    /**
     * Currently opened file's metadata.
     */
    private Metadata mMetadata;

    /**
     * Currently opened file's contents.
     */
    private DriveContents mDriveContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_google);

        uploadButton = (Button) findViewById(R.id.buttonUploadStatistics);
        downloadButton = (Button) findViewById(R.id.buttonDownloadStatistics);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ifilename = Environment.getDataDirectory().getPath() + "/data/dreamteam.focus/databases/database";
                i = new File(ifilename);

                try {
                    final String temp = Environment.getDataDirectory().getPath() + "/data/dreamteam.focus/databases/database2";
                    InputStream mInput = new FileInputStream(ifilename);
                    String outFileName = temp;
                    OutputStream mOutput = new FileOutputStream(outFileName);
                    byte[] mBuffer = new byte[1024];
                    int mLength;
                    while ((mLength = mInput.read(mBuffer))>0)
                    {
                        mOutput.write(mBuffer, 0, mLength);
                    }
                    mOutput.flush();
                    mOutput.close();
                    mInput.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Scanner s = null;
//                try {
//                    s = new Scanner(i);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                while (s.hasNextLine()){
//                    Log.e("TAG", s.nextLine());
//                }
                fileOperation = true;

                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(driveContentsCallback);

            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileOperation = false;

                // create new contents resource
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(driveContentsCallback);
            }
        });
    }

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {

            if (result.getStatus().isSuccess()) {

                if (fileOperation == true) {

                    CreateFileOnGoogleDrive(result);

                } else {

                    OpenFileFromGoogleDrive();

                }
            }
        }
    };

    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result){

        final DriveContents driveContents = result.getDriveContents();
        Log.e("TAG", "Check-point 2");
        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                InputStream input = null;
                try {
                    input = new FileInputStream(i);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.e("TAG", "Check-point 3");
                OutputStream output = driveContents.getOutputStream();
                Log.e("TAG", "Check-point 3.1");
                // transfer bytes from the Input File to the Output File
                byte[] buffer = new byte[1024];
                int length;
                try {
                    length = input.read(buffer);
                    Log.e("TAG", "Check-point 3.2");

                    while (length>0) {
                        Log.e("TAG", "Check-point: "+ buffer);
                        output.write(buffer, 0, length);
                        length = input.read(buffer);
                    }
                    Log.e("TAG", "Check-point 4");
                    output.flush();
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Date currentTime = Calendar.getInstance().getTime();
                Log.e("TAG", "Check-point 5");

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("Focus.txt")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
    ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (result.getStatus().isSuccess()) {

                Toast.makeText(getApplicationContext(), "Backup File Created with "+
                        result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

            }

            return;

        }
    };


    public void OpenFileFromGoogleDrive(){

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { "text/plain", "text/html" })
             .build(mGoogleApiClient);
        try {

            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

        } catch (IntentSender.SendIntentException e) {

            Log.w(TAG, "Unable to send intent", e);
        }

    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

//                    mFileId = (DriveId) data.getParcelableExtra(
//                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//
//                    Log.e("file id", mFileId.getResourceId() + "");
//
//                    URL url = null;
//                    try {
//                        url = new URL("https://drive.google.com/file/d/1CzxFu0UIKVBmpZJHecNnVG_jXr3aEvaz/view");
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.e("TAG1", mFileId.getResourceId().toString());
//
//                    DriveFile driveFile = mFileId.asDriveFile();
//
//                    try {
//                        //url = new URL("https://drive.google.com/open?id="+ mFileId.getResourceId().toString());
//                        URLConnection yc = url.openConnection();
//                        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//                        String inputLine;
//                        while ((inputLine = in.readLine()) != null)
//                            Log.e("TAG3", inputLine);
//                        in.close();
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//

//                    try {
//                        FileInputStream input = new FileInputStream(f);
//                        FileOutputStream output = new FileOutputStream(i, false);
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        try {
//                            length = input.read(buffer);
//                            Log.e("TAG", "Check-point 3.2");
//
//                            while (length>0) {
//                                Log.e("TAG", "Check-point: "+ buffer);
//                               // output.write(buffer, 0, length);
//                                length = input.read(buffer);
//                            }
//                            Log.e("TAG", "Check-point 4");
//                            output.flush();
//                            output.close();
//                            input.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

//                    final String ifilename = Environment.getDataDirectory().getPath() + "/data/dreamteam.focus/databases/database";
//                    File i = new File(ifilename);
//                    try {
//                        FileOutputStream output = new FileOutputStream(i, false);
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        try {
//                            length = input.read(buffer);
//                            Log.e("TAG", "Check-point 3.2");
//
//                            while (length>0) {
//                                Log.e("TAG", "Check-point: "+ buffer);
//                                output.write(buffer, 0, length);
//                                length = input.read(buffer);
//                            }
//                            Log.e("TAG", "Check-point 4");
//                            output.flush();
//                            output.close();
//                            input.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);



//                    Log.e("file id", mFileId.getResourceId() + "");
//
//                    URL url = null;
//                    try {
//                        url = new URL("https://drive.google.com/uc?export=download&id="+ mFileId.getResourceId());
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url.toString()));
//
//                    Log.e("TAG13", i.getData().toString());
//                    startActivity(i);
//
//                    OutputStream outputStream = new ByteArrayOutputStream();
//                    driveService.files().export(mFileId, "text/html")
//                            .executeMediaAndDownloadTo(outputStream);



                    try {
                        final String temp = Environment.getDataDirectory().getPath() + "/data/dreamteam.focus/databases/database2";
                        File tempFile = new File(temp);
                        BufferedReader bf = new BufferedReader(new FileReader(tempFile));

                        final String iFile = Environment.getDataDirectory().getPath() + "/data/dreamteam.focus/databases/database";

                        File file = new File(iFile);

                        tempFile.renameTo(file);

                        InputStream mInput = new FileInputStream(file);
                        String outFileName = temp;
                        OutputStream mOutput = new FileOutputStream(outFileName);
                        byte[] mBuffer = new byte[1024];
                        int mLength;
                        while ((mLength = mInput.read(mBuffer))>0)
                        {
                            mOutput.write(mBuffer, 0, mLength);
                        }
                        mOutput.flush();
                        mOutput.close();
                        mInput.close();



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //TEMPORARY CODE ENDS HERE

                    Log.e("file id", mFileId.getResourceId() + "");

                    String url = "https://drive.google.com/open?id="+ mFileId.getResourceId();

                    //New code starts here
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                String url = "https://drive.google.com/open?id="+ mFileId.getResourceId();
                                URL website = new URL(url);
                                File temp = new File(Environment.getExternalStorageDirectory() + "/Download/" + "Focus.txt");
                                //temp.createNewFile();
                                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                                FileOutputStream fos = new FileOutputStream(temp);
                                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                FileInputStream input = new FileInputStream(temp);
                                byte[] buffer = new byte[1024];
                                int length;

                                length = input.read(buffer);
                                Log.e("TAG", "Check-point 3.2");
                                while (length > 0) {
                                    Log.e("TAGC", "Check-point: " + buffer);
                                    length = input.read(buffer);
                                }

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    t.start();
                    //File file = new File(Environment.getDataDirectory().getPath() + "/data/com.android.providers.downloads.documents/document/");
                    File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + "Focus.txt");
                    file.setReadable(true);


                    if(file.exists()) {
                        Log.e("TAG@", file.exists() + "");
//                        try {
//                            FileInputStream fis = new FileInputStream(file);
//                            ReadableByteChannel rbc = Channels.newChannel(fis);
//                            FileOutputStream fos = new FileOutputStream(temp);
//                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//                            FileInputStream input = new FileInputStream(temp);
//                            byte[] buffer = new byte[1024];
//                            int length;
//
//                            length = input.read(buffer);
//                            Log.e("TAG", "Check-point 3.2");
//                            while (length > 0) {
//                                Log.e("TAGC", "Check-point: " + buffer);
//                                length = input.read(buffer);
//                            }
////                        br.close();
//                        }
//                        catch (IOException e) {
//                            //You'll need to add proper error handling here
//                        }
                    }
//Read text from file
                    StringBuilder text = new StringBuilder();



                    //New Code ends here

//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);i

                        Toast.makeText(getApplicationContext(), "Google Drive Data Restored Successfully", Toast.LENGTH_LONG).show();

                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }


    /**
     * Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack,
     * with user input going to it.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * It invoked when Google API client connected
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Toast.makeText(getApplicationContext(), "Google Drive Connection Successful", Toast.LENGTH_LONG).show();
    }

    /**
     * It invoked when connection suspended
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {

        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onBackPressed() {
        this.onStop();
        super.onBackPressed();
    }
}
package com.example.taskmanager.invoke;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.taskmanager.values.FunctionCall;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.taskmanager.values.constants.APK_FILE_DOWNLOADED;
import static com.example.taskmanager.values.constants.APK_FILE_NOT_FOUND;
import static com.example.taskmanager.values.constants.FTP_HOST;
import static com.example.taskmanager.values.constants.FTP_PASS;
import static com.example.taskmanager.values.constants.FTP_PORT;
import static com.example.taskmanager.values.constants.FTP_USER;


public class FTPAPI {

    private FunctionCall fcall = new FunctionCall();
    @SuppressLint("StaticFieldLeak")
    public class Download_apk  extends AsyncTask<String, Integer, String> {
        boolean downloadapk=false, file_found=false;
        Handler handler;
        ProgressDialog progressDialog;
        String mobilepath = fcall.filepath("ApkFolder") + File.separator;
        String update_version;

        public Download_apk(Handler handler, ProgressDialog progressDialog, String update_version) {
            this.handler = handler;
            this.progressDialog = progressDialog;
            this.update_version = update_version;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fcall.showprogressdialog("Please wait to complete...", progressDialog, "Downloading APK");
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            long read = 0;

            fcall.logStatus("Main_Apk 1");
            FTPClient ftp_1 = new FTPClient();
            fcall.logStatus("Main_Apk 2");
            try {
                fcall.logStatus("Main_Apk 3");
                ftp_1.connect(FTP_HOST, FTP_PORT);
                fcall.logStatus("Main_Apk 4");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fcall.logStatus("Main_Apk 5");
                ftp_1.login(FTP_USER, FTP_PASS);
                downloadapk = ftp_1.login(FTP_USER, FTP_PASS);
                fcall.logStatus("Main_Apk 6");
            } catch (FTPConnectionClosedException e) {
                e.printStackTrace();
                try {
                    downloadapk = false;
                    ftp_1.disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (downloadapk) {
                fcall.logStatus("Apk download billing_file true");
                try {
                    fcall.logStatus("Main_Apk 7");
                    ftp_1.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp_1.enterLocalPassiveMode();
                    fcall.logStatus("Main_Apk 8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fcall.logStatus("Main_Apk 9");
                    ftp_1.changeWorkingDirectory("/Android/Apk/");
                    fcall.logStatus("Main_Apk 10");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fcall.logStatus("Main_Apk 11");
                    FTPFile[] ftpFiles = ftp_1.listFiles("/Android/Apk/");
                    fcall.logStatus("Main_Apk 12");
                    int length = ftpFiles.length;
                    fcall.logStatus("Main_Apk 13");
                    fcall.logStatus("Main_Apk_length = " + length);
                    String namefile;
                    long filelength = 0;
                    for (FTPFile ftpFile : ftpFiles) {
                        namefile = ftpFile.getName();
                        fcall.logStatus("Main_Apk_namefile : " + namefile);
                        boolean isFile = ftpFile.isFile();
                        if (isFile) {
                            fcall.logStatus("Main_Apk_File: " + "Taskmanager_" + update_version + ".apk");
                            if (namefile.equals("Taskmanager_" + update_version + ".apk")) {
                                fcall.logStatus("Main_Apk File found to download");
                                filelength = ftpFile.getSize();
                                file_found = true;
                                break;
                            }
                        }
                    }
                    if (file_found) {
                        File file = new File(mobilepath + "Taskmanager_"+update_version+".apk");
                        fcall.logStatus("FTP File length: "+filelength);
                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                        InputStream inputStream = ftp_1.retrieveFileStream("/Android/Apk/" + "Taskmanager_"+update_version+".apk");
                        byte[] bytesIn = new byte[1024];
                        while ((count = inputStream.read(bytesIn)) != -1) {
                            read += count;
                            publishProgress((int)((read*100)/filelength));
                            outputStream.write(bytesIn, 0, count);
                        }
                        inputStream.close();
                        outputStream.close();

                        if (ftp_1.completePendingCommand()) {
                            fcall.logStatus("Apk file Download successfully.");
                            handler.sendEmptyMessage(APK_FILE_DOWNLOADED);
                        }
                    } else handler.sendEmptyMessage(APK_FILE_NOT_FOUND);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                ftp_1.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }
}

package com.example.askQuestionPoll.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.askQuestionPoll.R;
import com.example.askQuestionPoll.core.interfaces.ImageSourcePickerInterface;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;

public class ImageSourceSelectionDialog extends Dialog {
    Context context;
    TextView gallery, camera;
    ImagePicker imagePicker;
    CameraImagePicker cameraImagePicker;
    String cameraPickerPath;
    ImageSourcePickerInterface sourceDialogInterface;

    public ImageSourceSelectionDialog(@NonNull Context context, ImageSourcePickerInterface sourceDialogInterface) {
        super(context);
        this.context = context;
        this.sourceDialogInterface = sourceDialogInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_source);

        gallery = findViewById(R.id.gallery_dialog_image_source);
        camera = findViewById(R.id.camera_dialog_image_source);


        gallery.setOnClickListener(view -> {
            sourceDialogInterface.requestPermission(true);
            dismiss();
        });
        camera.setOnClickListener(view -> {
            sourceDialogInterface.requestPermission(false);
            dismiss();
        });


    }

/*
    private void RequestPermission(int i) {

        if (i == 0) {
            //gallery choose
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            pickImageFromGallery();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                Toast.makeText(context, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                                openDialogOfSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        } else if (i == 1) {
            //camera choose
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            pickImageFromCamera();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                                openDialogOfSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void openDialogOfSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("This App needs permission to use this feature. You can grant them in app settings.");
        builder.setTitle("Need Permissions");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Goto Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            getOwnerActivity().startActivityForResult(intent, 103);
            dialog.dismiss();
        });
        AlertDialog diag = builder.create();
        //Display the message!
        diag.show();
        diag.setCanceledOnTouchOutside(true);
    }


    public void pickImageFromCamera() {
        cameraImagePicker = new CameraImagePicker(getOwnerActivity());
        cameraImagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                Log.e("Camera Message ", "" + list.get(0).getOriginalPath());
                Log.e("Camera Message ", "" + list.get(0).getSize());
                Extras.signUpImagePickUrl = list.get(0).getOriginalPath();
                SignupActivity.setupPickedImage(list.get(0).getOriginalPath());

            }

            @Override
            public void onError(String s) {
                Log.e("error", s);
            }
        });
        cameraPickerPath = cameraImagePicker.pickImage();
        Log.e("camera picker path", cameraPickerPath);
    }

    public void pickImageFromGallery() {

        imagePicker = new ImagePicker(getOwnerActivity());
        Log.e("Message", "Select Image");
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                Log.e("Gallery Message ", "" + list.get(0).getOriginalPath());
                Log.e("Gallery Message ", "" + list.get(0).getSize());
                Extras.signUpImagePickUrl = list.get(0).getOriginalPath();
                SignupActivity.setupPickedImage(list.get(0).getOriginalPath());

            }

            @Override
            public void onError(String s) {
                Log.e("error", s);
            }
        });
        imagePicker.pickImage();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Result", " Stage 1");
        if (resultCode == Activity.RESULT_OK) {
            Log.e("Result", " Stage 2 OK");
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                Log.e("Result", " Stage GALLERy");
                if (imagePicker == null) {
                    pickImageFromGallery();
                }
                imagePicker.submit(data);
            }
            if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                Log.e("Result", " Stage Camera");
                if (cameraImagePicker == null) {
                    Log.e("camera picker path", cameraPickerPath);
                    pickImageFromCamera();

                    cameraImagePicker.reinitialize(cameraPickerPath);

                    // OR in one statement
                    // imagePicker = new CameraImagePicker(Activity.this, outputPath);
                }
                cameraImagePicker.submit(data);
            }
        }
    }*/
}

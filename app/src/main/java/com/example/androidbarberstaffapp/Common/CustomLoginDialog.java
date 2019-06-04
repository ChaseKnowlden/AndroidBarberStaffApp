package com.example.androidbarberstaffapp.Common;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidbarberstaffapp.Interface.IDialogClickListener;
import com.example.androidbarberstaffapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomLoginDialog {
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.edt_user)
    TextInputEditText edt_user;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;

    public static CustomLoginDialog mDialog;
    public IDialogClickListener iDialogClickListener;

    public static CustomLoginDialog getInstance() {
        if (mDialog == null)
            mDialog = new CustomLoginDialog();
        return mDialog;
    }

    public void showLoginDialog(String title,
                                String positiveText,
                                String negativeText,
                                Context context,
                                final IDialogClickListener iDialogClickListener)
    {
        this.iDialogClickListener = iDialogClickListener;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login);

        ButterKnife.bind(this,dialog);
        if (!TextUtils.isEmpty(title))
        {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);
        }
        btn_login.setText(positiveText);
        btn_cancel.setText(negativeText);

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickPositiveButton(dialog,edt_user.getText().toString(),
                        edt_password.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickNegativeButton(dialog);
            }
        });
    }
}

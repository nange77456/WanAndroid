package com.dss.wanandroid.custom.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dss.wanandroid.R;
import com.dss.wanandroid.utils.NoParamPhone;

/**
 * 自定义输入框，圆角框+取消按钮
 */
public class EditTextPlus extends FrameLayout {
    /**
     * 输入框
     */
    private final EditText inputText;
    /**
     * 回车搜索的回调方法
     */
    private NoParamPhone enterSearchPhone;

    /**
     * 获取用户输入的文字
     * @return
     */
    public String getInput() {
        return inputText.getText().toString();
    }

    public void setEnterSearchPhone(NoParamPhone phone) {
        enterSearchPhone = phone;
    }

    /**
     * 给输入框设置文字
     * @param key
     */
    public void setInputText(String key){
        inputText.setText(key);
    }

    public EditTextPlus(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_edittext,this);

        inputText = view.findViewById(R.id.input);
        final ImageView cancelButton = view.findViewById(R.id.cancel);

        //监听用户输入，有输入则显示cancelButton
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    cancelButton.setVisibility(INVISIBLE);
                }else{
                    cancelButton.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //点击cancelButton删除用户输入
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.setText("");
            }
        });

        //设置回车键搜索
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH || (event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    //搜索回调
                    if(enterSearchPhone!=null){
                        enterSearchPhone.onPhone();
                    }
                    return true;
                }
                return false;
            }
        });

    }
}

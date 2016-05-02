package com.trychen.flyme.masscalculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Main extends Activity implements PopupWindow.OnDismissListener {
    private EditText massEditText;
    private ImageView indexImage;
    private Button btnCa;
    private Button btnClean;
    private LinearLayout buttonLayout;
    private TextView textWegiht;
    private LinearLayout rootView;
    private static boolean simpleMode = false;
    private boolean calculated = false;
    private boolean isSoftKeyBoard = false;
    public static List<Map<String, Object>> masses;
    private PerPopupWindows popupWindows;

    public static MassListViewAdapter massesAdapter;

    FormulaParse parse;
    Formula formula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        //读取配置数据
        read();

        //初始化界面
        initView();

        masses = new ArrayList<>();

        //检测3D Press
        Uri data = getIntent().getData();
        if (data != null && TextUtils.equals("forcetouch", data.getScheme())) {
            if (TextUtils.equals("/basic", data.getPath())) {
                simpleMode = true;
                forceOpenSoftKeyBoard();
            } else if (TextUtils.equals("/higher", data.getPath())) {
                simpleMode = false;
                forceOpenSoftKeyBoard();
            }
        }

        //限制分屏调整大小
        try {
            Class<?> clazz = Class.forName("meizu.splitmode.FlymeSplitModeManager");
            Method b = clazz.getMethod("getInstance", Context.class);
            Object instance = b.invoke(null, Main.this);
            Log.i("debug", "Class Name:   " + instance.getClass().getName());
            Method disableResize = clazz.getMethod("disableResize", Activity.class);
            disableResize.invoke(instance, Main.this);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        parse = new FormulaParse();


        massEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6) {
                    calculate();
                }
                return true;
            }
        });

        //清除按钮
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        btnCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });


        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                changeView(heightDiff > 100);
                Log.i("heightDiff", String.valueOf(heightDiff));
            }
        });
        //初始化MassesLayout
        massesAdapter = new MassListViewAdapter(Main.this);

        textWegiht.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println(calculated);
                if (calculated) {
                    popupWindows.show();
                    full(true);
                    return true;
                } else return false;
            }
        });


    }

    public void initView() {
        rootView = (LinearLayout) findViewById(R.id.rootLayout);
        massEditText = (EditText) findViewById(R.id.massEditText);
        massEditText.setInputType(EditorInfo.IME_ACTION_DONE);

        indexImage = (ImageView) findViewById(R.id.indexImage);
        indexImage.setVisibility(View.VISIBLE);

        btnCa = (Button) findViewById(R.id.btnCau);

        btnClean = (Button) findViewById(R.id.btnClean);

        textWegiht = (TextView) findViewById(R.id.textWegiht);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Class<?> clazz;
        try {
            clazz = Class.forName("meizu.splitmode.FlymeSplitModeManager");
            Method b = clazz.getMethod("getInstance", Context.class);
            Object instance = b.invoke(null, Main.this);
            Method m = clazz.getMethod("isSplitMode");
            boolean result = (boolean) m.invoke(instance);
            changeView(result);
        } catch (ClassNotFoundException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save(simpleMode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            boolean[] booleans = {simpleMode};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle(R.string.help_setting);
            builder.setMultiChoiceItems(new String[]{getResources().getString(R.string.simple_weigh)}, booleans, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (which == 0) {
                        save(isChecked);
                        simpleMode = isChecked;
                    }
                }
            });
            builder.setPositiveButton(R.string.confirm, null);
            builder.setNegativeButton(R.string.more, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Main.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder.setView(R.layout.about);
                    builder.setPositiveButton(R.string.confirm, null);
                    builder.create().show();
                }
            });
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String inp = "";
    DecimalFormat twoDForm = new DecimalFormat("#.##");

    private void calculate() {
        inp = massEditText.getText().toString();
        if (inp.length() == 0) {
            return;
        }
        parse.setInput(inp);
        int nFormulas = parse.parse();
        if (nFormulas == 0) {
            textWegiht.setText("N/A");
            massEditText.setText("");
            massEditText.setHint(R.string.wrong);
            massEditText.setHintTextColor(Color.parseColor("#508aeb"));
            textWegiht.setText("N/A", TextView.BufferType.NORMAL);
            massEditText.setText("");
            massEditText.setHint(R.string.please_typein);
            massEditText.setHintTextColor(Color.parseColor("#33517bd1"));
            calculated = false;
        } else if (nFormulas == 1) {
            show();
        } else {
            show();
        }
    }

    public void show() {
        formula = parse.getFormula();
        String out = twoDForm.format(formula.getMass());
        int si = String.valueOf((int) formula.getMass()).length();
        SpannableString styledText = new SpannableString(out);
        styledText.setSpan(new TextAppearanceSpan(this, R.style.numble), 0, si, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(this, R.style.decimal), si, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textWegiht.setText(styledText, TextView.BufferType.SPANNABLE);

        masses.clear();
        for (int i = 0; i < formula.symbs.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", formula.getName(i));
            map.put("num", String.valueOf(formula.nums.get(i)));
            map.put("percents", formula.getMassPercent(formula.vals.get(i)) + "%");
            masses.add(map);
        }

        popupWindows = new PerPopupWindows(this, massEditText.getText().toString(), out, getLayoutInflater(), rootView);
        popupWindows.setOnDismissListener(this);

        calculated = true;
        System.gc();
    }

    private void save(boolean b) {

        String content = b ? "true" : "false";
        try {
            /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的，
             * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的
             *   public abstract FileOutputStream openFileOutput(String name, int mode)
             *   throws FileNotFoundException;
             * openFileOutput(String name, int mode);
             * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名
             *          该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt
             * 第二个参数，代表文件的操作模式
             *          MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖
             *          MODE_APPEND  私有   重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件
             *          MODE_WORLD_READABLE 公用  可读
             *          MODE_WORLD_WRITEABLE 公用 可读写
             *  */
            FileOutputStream outputStream = openFileOutput("config.dat",
                    Activity.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    private void read() {
        try {
            FileInputStream inputStream = this.openFileInput("config.dat");
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());
            simpleMode = content.startsWith("true");
        } catch (FileNotFoundException e) {
            save(isSimpleMode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSimpleMode() {
        return simpleMode;
    }

    public void forceOpenSoftKeyBoard() {
        massEditText.setFocusable(true);
        massEditText.setFocusableInTouchMode(true);
        massEditText.requestFocus();

        InputMethodManager inputManager =
                (InputMethodManager) massEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.showSoftInput(massEditText, 0);
    }

    public void clear() {
        masses.clear();
        textWegiht.setText("N/A", TextView.BufferType.NORMAL);
        massEditText.setText("");
        massEditText.setHint(R.string.please_typein);
        massEditText.setHintTextColor(Color.parseColor("#33517bd1"));
        calculated = false;
    }

    public void changeView(boolean b) {
        if (b) {
            if (isSoftKeyBoard) return;
            if (!calculated) {
                textWegiht.setTextSize(TypedValue.COMPLEX_UNIT_SP, 90);
            } else {
                show();
            }
            indexImage.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);
            isSoftKeyBoard = true;
        } else {
            if (!isSoftKeyBoard) return;
            indexImage.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.GONE);
            if (!calculated) {
                textWegiht.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
            } else {
                textWegiht.setText(twoDForm.format(formula.getMass()));
                textWegiht.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
            }
            if (calculated&&popupWindows!=null&&popupWindows.isShowing()){
                popupWindows.dismiss();
            }
            isSoftKeyBoard = false;
            massEditText.setHintTextColor(getResources().getColor(R.color.hint_text_color));
        }
    }

    @Override
    public void onDismiss() {
        full(false);
    }

    private void full(boolean enable) {
        if (enable) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.mz_theme_color_dodgerblue_dark));
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            getWindow().setAttributes(lp);
        } else {
            getWindow().setStatusBarColor(0xff4777eb);
//            getWindow().setStatusBarColor(Resources.getSystem().getColor(R.color.mz_theme_color_dodgerblue));
//            WindowManager.LayoutParams attr = getWindow().getAttributes();
//            attr.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setAttributes(attr);
        }
    }
}

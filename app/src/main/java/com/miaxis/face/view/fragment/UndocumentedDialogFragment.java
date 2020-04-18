package com.miaxis.face.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.adapter.NationAdapter;
import com.miaxis.face.util.PatternUtil;

import java.util.Arrays;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UndocumentedDialogFragment extends BaseDialogFragment {

    @BindArray(R.array.nation_list)
    String[] nationList;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;
    @BindView(R.id.rv_nation)
    RecyclerView rvNation;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_error_name)
    TextView tvErrorName;
    @BindView(R.id.tv_error_card_number)
    TextView tvErrorCardNumber;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    private Unbinder bind;
    private NationAdapter<String> nationAdapter;
    private OnDialogButtonClickListener listener;

    public static UndocumentedDialogFragment newInstance(OnDialogButtonClickListener listener) {
        UndocumentedDialogFragment undocumentedDialogFragment = new UndocumentedDialogFragment();
        undocumentedDialogFragment.setListener(listener);
        return undocumentedDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        hideNavigationBar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_undocumented_dialog, container);
        bind = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    protected void initView() {
        //TODO:
        etName.setText("唐一非");
        etCardNumber.setText("340823199601021913");
        llRoot.setOnClickListener(v -> hideInputMethod(llRoot));
        btnCancel.setOnClickListener(view -> {
            dismiss();
            listener.onCancel();
        });
        btnConfirm.setOnClickListener(view -> {
            if (checkLastInput()) {
                dismiss();
                listener.onConfirm(etName.getText().toString(), etCardNumber.getText().toString(), nationAdapter.getSelecNation());
            }
        });
        nationAdapter = new NationAdapter<>(getActivity(), Arrays.asList(nationList));
        rvNation.setLayoutManager(new GridLayoutManager(getActivity(), 8));
        rvNation.setAdapter(nationAdapter);
        nationAdapter.selectData(0);
        nationAdapter.setOnItemClickListener((view, position) -> nationAdapter.selectData(position));
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    tvErrorName.setVisibility(View.INVISIBLE);
                }
            }
        });
        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (!inputText.isEmpty()) {
                    String firstChar = inputText.substring(0, 1);
                    if (TextUtils.equals(firstChar, "7") || TextUtils.equals(firstChar, "8")) {
                        nationAdapter.selectData(nationAdapter.getDataList().size() - 1);
                    } else if (TextUtils.equals(nationAdapter.getSelecNation(), "其他")) {
                        nationAdapter.selectData(0);
                    }
                    tvErrorCardNumber.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    public void setListener(OnDialogButtonClickListener listener) {
        this.listener = listener;
    }

    private boolean checkLastInput() {
        if (etName.getText().toString().isEmpty()) {
            tvErrorName.setVisibility(View.VISIBLE);
            return false;
        }
        if (etCardNumber.getText().toString().isEmpty() || !PatternUtil.isIDNumber(etCardNumber.getText().toString())) {
            tvErrorCardNumber.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public interface OnDialogButtonClickListener {
        void onCancel();

        void onConfirm(String name, String cardNumber, String nation);
    }

}

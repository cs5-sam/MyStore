package android.example.ecom.fragments;

import android.content.Intent;
import android.example.ecom.activity.HomeActivity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.example.ecom.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText signupEmail, signupName, signupPassword, signupConfirmPassword;
    private TextView signinText;
    private Button signupBtn;
    private ImageButton cancelBtn;
    private ProgressBar signupProgressBar;
    private String email, name, password, confirmPassword;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private FrameLayout parentFrameLayout;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;


    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup, container, false);

        // INITIALIZE VIEWS
        signinText = view.findViewById(R.id.tv_signin_account);
        signupEmail = view.findViewById(R.id.signup_email);
        signupName = view.findViewById(R.id.signup_name);
        signupPassword = view.findViewById(R.id.signup_password);
        signupConfirmPassword = view.findViewById(R.id.signup_confirm_password);
        signupBtn = view.findViewById(R.id.signup_btn);
        signupProgressBar = view.findViewById(R.id.signup_progressBar);
        cancelBtn = view.findViewById(R.id.signup_close_btn);

        // INITIALIZE FRAME LAYOUT
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // VALIDATE AND VALIDATE DATA
                email = signupEmail.getText().toString();
                name = signupName.getText().toString();
                password = signupPassword.getText().toString();
                confirmPassword = signupConfirmPassword.getText().toString();

                if(email.equals("") || name.equals("") || password.equals("") || confirmPassword.equals("")){
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else{
                    checkEmailPassword();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }

    // CHECK EMAIL PASSWORD
    private void checkEmailPassword() {

//        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
//        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if(signupEmail.getText().toString().matches(emailPattern)){
            if (signupPassword.getText().toString().equals(signupConfirmPassword.getText().toString())){

                signupProgressBar.setVisibility(View.VISIBLE);
                signupBtn.setVisibility(View.INVISIBLE);

                auth.createUserWithEmailAndPassword(signupEmail.getText().toString(),signupPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Account Created!", Toast.LENGTH_SHORT).show();

                                    Map<Object,String> userData = new HashMap<>();
                                    userData.put("fullname",signupName.getText().toString());

                                    // ADDING DATA TO FIRESTORE
                                    firestore.collection("USERS")
                                            .add(userData)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(getActivity(), "Registration Complete!", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(getActivity(), HomeActivity.class);
                                                        startActivity(i);
                                                        getActivity().finish();
                                                    }else{
                                                        signupProgressBar.setVisibility(View.INVISIBLE);
                                                        signupBtn.setVisibility(View.VISIBLE);
                                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            signupProgressBar.setVisibility(View.INVISIBLE);
                                            signupBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signupProgressBar.setVisibility(View.INVISIBLE);
                        signupBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                signupProgressBar.setVisibility(View.INVISIBLE);
                signupBtn.setVisibility(View.VISIBLE);
                signupConfirmPassword.setError("Password does not match!");
            }
        }else{
            signupProgressBar.setVisibility(View.INVISIBLE);
            signupBtn.setVisibility(View.VISIBLE);
            signupEmail.setError("Invalid email");
        }
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
package android.example.ecom.fragments;

import android.content.Intent;
import android.example.ecom.activity.HomeActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import static android.example.ecom.activity.RegisterActivity.onResetPasswordFragment;

public class SigninFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView signupText, forgotPassword;
    private EditText signinEmail, signinPassword;
    private Button signinBtn;
    private ProgressBar signinBar;
    private ImageButton cancelBtn;

    private String email, password;

    private FirebaseAuth auth;

    private FrameLayout parentFrameLayout;

    public SigninFragment() {
        // Required empty public constructor
    }

    public static SigninFragment newInstance(String param1, String param2) {
        SigninFragment fragment = new SigninFragment();
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
        View view = inflater.inflate(R.layout.fragment_signin,container,false);

        signupText = view.findViewById(R.id.tv_signunp_account);
        signinEmail = view.findViewById(R.id.signin_email);
        signinPassword = view.findViewById(R.id.signin_password);
        signinBar = view.findViewById(R.id.signin_progressBar);
        signinBtn = view.findViewById(R.id.signin_btn);
        cancelBtn = view.findViewById(R.id.signin_close_btn);
        forgotPassword = view.findViewById(R.id.signin_forgot_password);

        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        auth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignupFragment());
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = signinEmail.getText().toString();
                password = signinPassword.getText().toString();

                if(email.equals("") || password.equals("")){
                    Toast.makeText(getActivity(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }else{
                    userSignin();
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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });
    }

    private void userSignin() {

        signinBtn.setVisibility(View.INVISIBLE);
        signinBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            signinBtn.setVisibility(View.VISIBLE);
                            signinBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            startActivity(i);
                            getActivity().finish();

                        }
                        else{

                            signinBtn.setVisibility(View.VISIBLE);
                            signinBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                signinBtn.setVisibility(View.VISIBLE);
                signinBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
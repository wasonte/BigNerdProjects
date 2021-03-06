package bignerd.jac.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER = "cheater";

    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mBackButton;
    private ImageButton mNextButtton;
    private TextView mQuestionTextView;

    private TextView mApiLevelTextView;

    private boolean[] mCheatedOnQuestion;

    private Question[] mQuestionBank = new Question[]{
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "onCreate(Bundle) called");

        mCheatedOnQuestion = new boolean[mQuestionBank.length];
        for(int i = 0; i < mCheatedOnQuestion.length; i++){
            mCheatedOnQuestion[i] = false;
        }

        mTrueButton = (Button)findViewById(R.id.true_button);
        mFalseButton = (Button)findViewById(R.id.false_button);
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mBackButton = (ImageButton)findViewById(R.id.back_button);
        mNextButtton = (ImageButton)findViewById(R.id.next_button);

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mApiLevelTextView = (TextView)findViewById(R.id.api_level_text_view);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex != 0){
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                } else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
            }
        });

        mNextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this , answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatedOnQuestion[mCurrentIndex] = savedInstanceState.getBoolean(KEY_CHEATER, false);
        }
        updateQuestion();

        // API level text
        mApiLevelTextView.setText("API level " + Build.VERSION.SDK_INT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState(outState) called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBoolean(KEY_CHEATER, mCheatedOnQuestion[mCurrentIndex]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    protected void updateQuestion(){
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
    }

    protected void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (mCheatedOnQuestion[mCurrentIndex] == true){
            messageResId = R.string.judgment_toast;
        } else{
            if (userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT){
            if (data == null){
                return;
            }
            mCheatedOnQuestion[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }
}

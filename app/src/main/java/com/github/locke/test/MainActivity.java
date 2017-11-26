package com.github.locke.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.github.locke.library.CollapsibleTextView;

public class MainActivity extends AppCompatActivity {

    CollapsibleTextView textView;
    AppCompatSeekBar mSeekWidth, mSeekHeight;

    String page = "Being in a relationship can be a full-time job. Sometimes, it can become so overwhelming and consuming that you lose your own voice and sense of ownership. You want freedom and you think your partner is not making you happy enough. You question why you are in the relationship at all.\n" +
            "\n" +
            "Yes, we all need that moment to feel uneasy and track our direction in whatever relationship we’re in. These breaks can be beneficial to becoming the person you want to be. Here are some of the positive things that can come out of those moments of indecision.\n" +
            "\n" +
            "1. You gain a different perspective on who your partner is\n" +
            "You are not so absorbed about them. You see them for their flaws and perfections. Since you can rely on your instincts and find other pleasures, rather than just within the relationship itself, you understand your partner better. This knowledge will prove beneficial in the way you treat them.\n" +
            "\n" +
            "2. You begin to acknowledge other worthy activities\n" +
            "Now that you want to hang out with other people, you actually appreciate your partner more and try fun new things with them. You can come up with fun things to do on your own and get your partner involved along the way.\n" +
            "\n" +
            "3. Your life switches from many fantasies to many realities\n" +
            "Yes, your relationship becomes real to you. It is not guess work or something that is to be planned ahead on paper. No fantasies, no fiction, but realities that will push you make the strong decision of pushing ahead or letting go.\n" +
            "\n" +
            "4. You understand the missing holes\n" +
            "Every relationship has some missing holes that need to be filled. It could be in terms of spirituality, finance, emotions, or mentality. When you are lost, you can understand these missing holes and find ways to fill them. It’s not just about the missing holes in the relationship, but the ones you find in yourself. What are your weaknesses? How can you be more purposeful and happ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        mSeekWidth = findViewById(R.id.seek_width);
        mSeekHeight = findViewById(R.id.seek_height);
        textView.setText(page);
        initSeekBars();

    }

    private void initSeekBars() {
        final float MAX_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics());
        final float MAX_HEIGHT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());

        mSeekWidth.setMax(100);
        mSeekWidth.setProgress(50);
        mSeekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int width = (int) (MAX_WIDTH * (progress / 100f));
                ViewGroup.LayoutParams lp = textView.getLayoutParams();
                lp.width = width;
                textView.setLayoutParams(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekHeight.setMax(100);
        mSeekHeight.setProgress(25);
        mSeekHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int height = (int) (MAX_HEIGHT * (progress / 100f));
                ViewGroup.LayoutParams lp = textView.getLayoutParams();
                lp.height = height;
                textView.setLayoutParams(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

package com.example.quiz;

import java.util.ArrayList;
import java.util.Collections;

public class FiftyFiftyHelper {

        private ArrayList<String> answerOptions;

        public FiftyFiftyHelper(ArrayList<String> answerOptions) {
            this.answerOptions = answerOptions;
        }


    public ArrayList<String> getModifiedOptions() {
            ArrayList<String> modifiedOptions = new ArrayList<String>(answerOptions);
            Collections.shuffle(modifiedOptions);
            modifiedOptions = new ArrayList<String>(modifiedOptions.subList(0, 2));
            return modifiedOptions;
        }
    }



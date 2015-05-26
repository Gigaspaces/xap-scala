package com.gigaspaces.verifier;

import com.gigaspaces.common.Data;
import java.lang.Math;

public class VerifierEngine {

    private static double VERIFY_THRESHOLD = 0.9;

    public boolean isVerified(Data data) {
        return Math.random() < VERIFY_THRESHOLD;
    }
}

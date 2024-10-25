package com.voinearadu.utils.lambda.lambda;


public interface ReturnArgsLambdaExecutor<R, A, B> {
    R execute(A a, B b);
}
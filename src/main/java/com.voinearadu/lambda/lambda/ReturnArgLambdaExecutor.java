package com.voinearadu.lambda.lambda;


public interface ReturnArgLambdaExecutor<R, A> {
    R execute(A a);
}
package io.github.zimoyin.qqbot;

import io.github.zimoyin.qqbot.utils.Async;
import io.vertx.core.Future;
import kotlin.Unit;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Job;

/**
 * 使用协程
 */
public class CoroutineTest {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.out.println(Thread.currentThread().getName());

        /**
         * 协程,适合执行IO 操作
         */
        Job coroutineIO = Async.createCoroutineIO(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        /**
         * 协程,适合执行CPU 操作
         */
        Job coroutineCPU = Async.createCoroutineCPU(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        /**
         * 协程,适合执行异步操作，并允许返回值
         */
        Future<Unit> coroutineTask = Async.createCoroutineTask(promise -> {
            System.out.println(Thread.currentThread().getName());
        });

        /**
         * 协程,适合执行异步操作，并允许返回值
         */
        Deferred<Unit> coroutineAsync = Async.createCoroutineAsync(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        /**
         * 协程,适合执行异步操作
         */
        Job coroutine = Async.createCoroutine(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        Async.createWorkerThread(() -> {
            System.out.println(Thread.currentThread().getName());
        });
    }
}

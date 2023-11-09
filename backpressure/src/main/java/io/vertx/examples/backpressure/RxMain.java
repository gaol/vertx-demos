/*
 *  Copyright (c) 2023 The original author or authors
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of Apache License v2.0 which
 *  accompanies this distribution.
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.vertx.examples.backpressure;

import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@SuppressWarnings("ALL")
public class RxMain {
    public static void main(String[] args) throws Exception {
        AtomicInteger got = new AtomicInteger();
        AtomicInteger dropped =new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);
        Flowable.intervalRange(1, 100, 1, 10, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(10, () -> {
                    System.out.println("Overflow");
                    dropped.incrementAndGet();
                }, BackpressureOverflowStrategy.DROP_LATEST)
//                .onBackpressureDrop(i -> {
//                    System.out.println("dropped: " + i);
//                    dropped.incrementAndGet();
//                })
//                .onBackpressureBuffer()
                .observeOn(Schedulers.computation(), false, 20)
                .subscribe(e -> {
                    TimeUnit.MILLISECONDS.sleep(50);
                    System.out.println("Got: " + e);
                    got.incrementAndGet();
                }, Throwable::printStackTrace, () -> {
                    System.out.println("Total got: " + got.get());
                    System.out.println("Total dropped: " + dropped.get());
                    latch.countDown();
                })
        ;
        latch.await();
    }
}

package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.multithreading.Job;
import de.zebrajaeger.sphere2cube.multithreading.JobExecutor;
import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import org.junit.jupiter.api.Test;

class ThreadTest {

    static class TestJob extends Job {
        private int id;

        public static TestJob of(int id) {
            return new TestJob(id);
        }

        public TestJob(int id) {
            this.id = id;
        }

        @Override
        public void exec() {
            System.out.printf("Start %d @ %d%n", id, System.currentTimeMillis());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
            System.out.printf("Stop %d @ %d%n", id, System.currentTimeMillis());
        }
    }

    @Test
    void maxJob1() throws InterruptedException {
        MaxJobQueueExecutor ex = new MaxJobQueueExecutor(2,1);
        Chronograph c = Chronograph.start();

        for (int i = 0; i < 8; ++i) {
            System.out.println("ADD JOB " + i);
            ex.addJob(TestJob.of(i));
        }

        ex.shutdown();
        System.out.println("-----------");
        System.out.println("expect ~8s");
        System.out.println(c.stop());
        System.out.println("-----------");
    }

    @Test
    void maxJob2() throws InterruptedException {
        MaxJobQueueExecutor ex = new MaxJobQueueExecutor(1,3);
        Chronograph c = Chronograph.start();

        for (int i = 0; i < 8; ++i) {
            System.out.println("ADD JOB " + i);
            ex.addJob(TestJob.of(i));
        }

        ex.shutdown();
        System.out.println("-----------");
        System.out.println("expect ~8s");
        System.out.println(c.stop());
        System.out.println("-----------");
    }

    @Test
    void maxJob3() throws InterruptedException {
        MaxJobQueueExecutor ex = new MaxJobQueueExecutor(2,3);
        Chronograph c = Chronograph.start();

        for (int i = 0; i < 8; ++i) {
            System.out.println("ADD JOB " + i);
            ex.addJob(TestJob.of(i));
        }

        ex.shutdown();
        System.out.println("-----------");
        System.out.println("expect ~4s");
        System.out.println(c.stop());
        System.out.println("-----------");
    }



}

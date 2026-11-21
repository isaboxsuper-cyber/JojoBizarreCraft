package com.jojomod.util;

import java.util.ArrayList;
import java.util.List;

/** Agenda tarefas para daqui a N ticks do servidor. */
public final class Scheduler {
    private static final class Task {
        int delay;
        final Runnable run;
        Task(int delay, Runnable run) { this.delay = delay; this.run = run; }
    }

    private static final List<Task> TASKS = new ArrayList<>();

    private Scheduler() {}

    public static void later(int ticks, Runnable r) {
        TASKS.add(new Task(Math.max(1, ticks), r));
    }

    public static void clear() {
        TASKS.clear();
    }

    public static void tick() {
        if (TASKS.isEmpty()) return;
        List<Task> snapshot = new ArrayList<>(TASKS);
        TASKS.clear();
        for (Task t : snapshot) {
            if (--t.delay <= 0) {
                try {
                    t.run.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                TASKS.add(t);
            }
        }
    }
}

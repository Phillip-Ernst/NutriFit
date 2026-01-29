package com.phillipe.NutriFit.WorkoutPlan.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedExercise {
    // Back exercises
    PULL_UP("Pull-up", ExerciseCategory.BACK),
    LAT_PULLDOWN("Lat Pulldown", ExerciseCategory.BACK),
    BARBELL_ROW("Barbell Row", ExerciseCategory.BACK),
    DUMBBELL_ROW("Dumbbell Row", ExerciseCategory.BACK),
    CABLE_ROW("Cable Row", ExerciseCategory.BACK),
    DEADLIFT("Deadlift", ExerciseCategory.BACK),
    T_BAR_ROW("T-Bar Row", ExerciseCategory.BACK),
    FACE_PULL("Face Pull", ExerciseCategory.BACK),

    // Chest exercises
    BENCH_PRESS("Bench Press", ExerciseCategory.CHEST),
    INCLINE_BENCH_PRESS("Incline Bench Press", ExerciseCategory.CHEST),
    DECLINE_BENCH_PRESS("Decline Bench Press", ExerciseCategory.CHEST),
    DUMBBELL_PRESS("Dumbbell Press", ExerciseCategory.CHEST),
    INCLINE_DUMBBELL_PRESS("Incline Dumbbell Press", ExerciseCategory.CHEST),
    PUSH_UP("Push-up", ExerciseCategory.CHEST),
    CHEST_FLY("Chest Fly", ExerciseCategory.CHEST),
    CABLE_CROSSOVER("Cable Crossover", ExerciseCategory.CHEST),
    DIP("Dip", ExerciseCategory.CHEST),

    // Biceps exercises
    BARBELL_CURL("Barbell Curl", ExerciseCategory.BICEPS),
    DUMBBELL_CURL("Dumbbell Curl", ExerciseCategory.BICEPS),
    HAMMER_CURL("Hammer Curl", ExerciseCategory.BICEPS),
    PREACHER_CURL("Preacher Curl", ExerciseCategory.BICEPS),
    CONCENTRATION_CURL("Concentration Curl", ExerciseCategory.BICEPS),
    CABLE_CURL("Cable Curl", ExerciseCategory.BICEPS),

    // Triceps exercises
    TRICEP_PUSHDOWN("Tricep Pushdown", ExerciseCategory.TRICEPS),
    SKULL_CRUSHER("Skull Crusher", ExerciseCategory.TRICEPS),
    OVERHEAD_TRICEP_EXTENSION("Overhead Tricep Extension", ExerciseCategory.TRICEPS),
    CLOSE_GRIP_BENCH_PRESS("Close-Grip Bench Press", ExerciseCategory.TRICEPS),
    TRICEP_DIP("Tricep Dip", ExerciseCategory.TRICEPS),
    TRICEP_KICKBACK("Tricep Kickback", ExerciseCategory.TRICEPS),

    // Shoulders exercises
    OVERHEAD_PRESS("Overhead Press", ExerciseCategory.SHOULDERS),
    DUMBBELL_SHOULDER_PRESS("Dumbbell Shoulder Press", ExerciseCategory.SHOULDERS),
    LATERAL_RAISE("Lateral Raise", ExerciseCategory.SHOULDERS),
    FRONT_RAISE("Front Raise", ExerciseCategory.SHOULDERS),
    REAR_DELT_FLY("Rear Delt Fly", ExerciseCategory.SHOULDERS),
    ARNOLD_PRESS("Arnold Press", ExerciseCategory.SHOULDERS),
    UPRIGHT_ROW("Upright Row", ExerciseCategory.SHOULDERS),
    SHRUG("Shrug", ExerciseCategory.SHOULDERS),

    // Hamstrings exercises
    ROMANIAN_DEADLIFT("Romanian Deadlift", ExerciseCategory.HAMSTRINGS),
    LEG_CURL("Leg Curl", ExerciseCategory.HAMSTRINGS),
    STIFF_LEG_DEADLIFT("Stiff-Leg Deadlift", ExerciseCategory.HAMSTRINGS),
    GOOD_MORNING("Good Morning", ExerciseCategory.HAMSTRINGS),
    NORDIC_CURL("Nordic Curl", ExerciseCategory.HAMSTRINGS),

    // Quads exercises
    SQUAT("Squat", ExerciseCategory.QUADS),
    FRONT_SQUAT("Front Squat", ExerciseCategory.QUADS),
    LEG_PRESS("Leg Press", ExerciseCategory.QUADS),
    LEG_EXTENSION("Leg Extension", ExerciseCategory.QUADS),
    LUNGE("Lunge", ExerciseCategory.QUADS),
    BULGARIAN_SPLIT_SQUAT("Bulgarian Split Squat", ExerciseCategory.QUADS),
    GOBLET_SQUAT("Goblet Squat", ExerciseCategory.QUADS),
    HACK_SQUAT("Hack Squat", ExerciseCategory.QUADS),

    // Glutes exercises
    HIP_THRUST("Hip Thrust", ExerciseCategory.GLUTES),
    GLUTE_BRIDGE("Glute Bridge", ExerciseCategory.GLUTES),
    CABLE_KICKBACK("Cable Kickback", ExerciseCategory.GLUTES),
    SUMO_DEADLIFT("Sumo Deadlift", ExerciseCategory.GLUTES),

    // Calves exercises
    CALF_RAISE("Calf Raise", ExerciseCategory.CALVES),
    SEATED_CALF_RAISE("Seated Calf Raise", ExerciseCategory.CALVES),
    DONKEY_CALF_RAISE("Donkey Calf Raise", ExerciseCategory.CALVES),

    // Core exercises
    PLANK("Plank", ExerciseCategory.CORE),
    CRUNCH("Crunch", ExerciseCategory.CORE),
    LEG_RAISE("Leg Raise", ExerciseCategory.CORE),
    RUSSIAN_TWIST("Russian Twist", ExerciseCategory.CORE),
    CABLE_CRUNCH("Cable Crunch", ExerciseCategory.CORE),
    AB_WHEEL_ROLLOUT("Ab Wheel Rollout", ExerciseCategory.CORE),
    DEAD_BUG("Dead Bug", ExerciseCategory.CORE),
    HANGING_LEG_RAISE("Hanging Leg Raise", ExerciseCategory.CORE),

    // Cardio exercises
    RUNNING("Running", ExerciseCategory.CARDIO),
    CYCLING("Cycling", ExerciseCategory.CARDIO),
    ROWING("Rowing", ExerciseCategory.CARDIO),
    JUMP_ROPE("Jump Rope", ExerciseCategory.CARDIO),
    STAIR_CLIMBER("Stair Climber", ExerciseCategory.CARDIO),
    ELLIPTICAL("Elliptical", ExerciseCategory.CARDIO),
    BURPEES("Burpees", ExerciseCategory.CARDIO),
    JUMPING_JACKS("Jumping Jacks", ExerciseCategory.CARDIO);

    private final String displayName;
    private final ExerciseCategory category;
}

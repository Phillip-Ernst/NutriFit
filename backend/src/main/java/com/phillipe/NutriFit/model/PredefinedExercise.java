package com.phillipe.NutriFit.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedExercise {
    // Back exercises
    PULL_UP("Pull-up", ExerciseCategory.BACK),
    CHIN_UP("Chin-up", ExerciseCategory.BACK),
    LAT_PULLDOWN("Lat Pulldown", ExerciseCategory.BACK),
    BARBELL_ROW("Barbell Row", ExerciseCategory.BACK),
    DUMBBELL_ROW("Dumbbell Row", ExerciseCategory.BACK),
    SMITH_MACHINE_BARBELL_ROW("Smith Machine Barbell Row", ExerciseCategory.BACK),
    SMITH_MACHINE_DEADLIFT("Smith Machine Deadlift", ExerciseCategory.BACK),
    CHEST_SUPPORTED_ROW("Chest-Supported Row", ExerciseCategory.BACK),
    CABLE_ROW("Cable Row", ExerciseCategory.BACK),
    DEADLIFT("Deadlift", ExerciseCategory.BACK),
    T_BAR_ROW("T-Bar Row", ExerciseCategory.BACK),
    FACE_PULL("Face Pull", ExerciseCategory.BACK),
    PULLDOWN_NEUTRAL_GRIP("Neutral-Grip Pulldown", ExerciseCategory.BACK),
    STRAIGHT_ARM_PULLDOWN("Straight-Arm Pulldown", ExerciseCategory.BACK),
    SINGLE_ARM_LAT_PULLDOWN("Single-Arm Lat Pulldown", ExerciseCategory.BACK),
    INVERTED_ROW("Inverted Row", ExerciseCategory.BACK),
    MEADOWS_ROW("Meadows Row", ExerciseCategory.BACK),
    BACK_EXTENSION("Back Extension", ExerciseCategory.BACK),

    // Chest exercises
    BENCH_PRESS("Bench Press", ExerciseCategory.CHEST),
    SMITH_MACHINE_BENCH_PRESS("Smith Machine Bench Press", ExerciseCategory.CHEST),
    INCLINE_BENCH_PRESS("Incline Bench Press", ExerciseCategory.CHEST),
    SMITH_MACHINE_INCLINE_BENCH_PRESS("Smith Machine Incline Bench Press", ExerciseCategory.CHEST),
    DECLINE_BENCH_PRESS("Decline Bench Press", ExerciseCategory.CHEST),
    SMITH_MACHINE_DECLINE_BENCH_PRESS("Smith Machine Decline Bench Press", ExerciseCategory.CHEST),
    DUMBBELL_PRESS("Dumbbell Press", ExerciseCategory.CHEST),
    INCLINE_DUMBBELL_PRESS("Incline Dumbbell Press", ExerciseCategory.CHEST),
    CABLE_PRESS("Cable Press", ExerciseCategory.CHEST),
    INCLINE_CABLE_PRESS("Incline Cable Press", ExerciseCategory.CHEST),
    DUMBBELL_PULLOVER("Dumbbell Pullover", ExerciseCategory.CHEST),
    PUSH_UP("Push-up", ExerciseCategory.CHEST),
    CHEST_FLY("Chest Fly", ExerciseCategory.CHEST),
    CABLE_FLY("Cable Fly", ExerciseCategory.CHEST),
    MACHINE_FLY("Machine Fly", ExerciseCategory.CHEST),
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
    SMITH_MACHINE_OVERHEAD_PRESS("Smith Machine Overhead Press", ExerciseCategory.SHOULDERS),
    MACHINE_PRESS("Machine Press", ExerciseCategory.SHOULDERS),
    DUMBBELL_SHOULDER_PRESS("Dumbbell Shoulder Press", ExerciseCategory.SHOULDERS),
    LATERAL_RAISE("Lateral Raise", ExerciseCategory.SHOULDERS),
    FRONT_RAISE("Front Raise", ExerciseCategory.SHOULDERS),
    REAR_DELT_FLY("Rear Delt Fly", ExerciseCategory.SHOULDERS),
    ARNOLD_PRESS("Arnold Press", ExerciseCategory.SHOULDERS),
    UPRIGHT_ROW("Upright Row", ExerciseCategory.SHOULDERS),
    SMITH_MACHINE_ROW("Smith Machine Row", ExerciseCategory.SHOULDERS),
    SHRUG("Shrug", ExerciseCategory.SHOULDERS),
    SMITH_MACHINE_SHRUG("Smith Machine Shrug", ExerciseCategory.SHOULDERS),

    // Hamstrings exercises
    ROMANIAN_DEADLIFT("Romanian Deadlift", ExerciseCategory.HAMSTRINGS),
    SMITH_MACHINE_ROMANIAN_DEADLIFT("Smith Machine Romanian Deadlift", ExerciseCategory.HAMSTRINGS),
    SINGLE_LEG_ROMANIAN_DEADLIFT("Single-Leg Romanian Deadlift", ExerciseCategory.HAMSTRINGS),
    SINGLE_LEG_DEADLIFT("Single-Leg Deadlift", ExerciseCategory.HAMSTRINGS),
    STIFF_LEG_DEADLIFT("Stiff-Leg Deadlift", ExerciseCategory.HAMSTRINGS),
    SMITH_MACHINE_STIFF_LEG_DEADLIFT("Smith Machine Stiff-Leg Deadlift", ExerciseCategory.HAMSTRINGS),
    SEATED_LEG_CURL("Seated Leg Curl", ExerciseCategory.HAMSTRINGS),
    GOOD_MORNING("Good Morning", ExerciseCategory.HAMSTRINGS),
    SMITH_MACHINE_GOOD_MORNING("Smith Machine Good Morning", ExerciseCategory.HAMSTRINGS),
    NORDIC_CURL("Nordic Curl", ExerciseCategory.HAMSTRINGS),

    // Quads exercises
    SQUAT("Squat", ExerciseCategory.QUADS),
    SMITH_MACHINE_SQUAT("Smith Machine Squat", ExerciseCategory.QUADS),
    FRONT_SQUAT("Front Squat", ExerciseCategory.QUADS),
    SMITH_MACHINE_FRONT_SQUAT("Smith Machine Front Squat", ExerciseCategory.QUADS),
    LEG_PRESS("Leg Press", ExerciseCategory.QUADS),
    LEG_EXTENSION("Leg Extension", ExerciseCategory.QUADS),
    LUNGE("Lunge", ExerciseCategory.QUADS),
    SMITH_MACHINE_LUNGE("Smith Machine Lunge", ExerciseCategory.QUADS),
    BULGARIAN_SPLIT_SQUAT("Bulgarian Split Squat", ExerciseCategory.QUADS),
    SMITH_MACHINE_BULGARIAN_SPLIT_SQUAT("Smith Machine Bulgarian Split Squat", ExerciseCategory.QUADS),
    GOBLET_SQUAT("Goblet Squat", ExerciseCategory.QUADS),
    HACK_SQUAT("Hack Squat", ExerciseCategory.QUADS),

    // Glutes exercises
    HIP_THRUST("Hip Thrust", ExerciseCategory.GLUTES),
    SMITH_MACHINE_HIP_THRUST("Smith Machine Hip Thrust", ExerciseCategory.GLUTES),
    GLUTE_BRIDGE("Glute Bridge", ExerciseCategory.GLUTES),
    SMITH_MACHINE_GLUTE_BRIDGE("Smith Machine Glute Bridge", ExerciseCategory.GLUTES),
    CABLE_KICKBACK("Cable Kickback", ExerciseCategory.GLUTES),
    SUMO_DEADLIFT("Sumo Deadlift", ExerciseCategory.GLUTES),
    SMITH_MACHINE_SUMO_DEADLIFT("Smith Machine Sumo Deadlift", ExerciseCategory.GLUTES),

    // Calves exercises
    CALF_RAISE("Calf Raise", ExerciseCategory.CALVES),
    SEATED_CALF_RAISE("Seated Calf Raise", ExerciseCategory.CALVES),
    DONKEY_CALF_RAISE("Donkey Calf Raise", ExerciseCategory.CALVES),

    // Forearms Exercises
    DUMBBELL_WRIST_CURL("Dumbbell Wrist Curl", ExerciseCategory.FOREARMS),
    BARBELL_WRIST_CURL("Barbell Wrist Curl", ExerciseCategory.FOREARMS),
    CABLE_WRIST_CURL("Cable Wrist Curl", ExerciseCategory.FOREARMS),
    REVERSE_WRIST_CURL("Reverse Wrist Curl", ExerciseCategory.FOREARMS),
    DUMBBELL_REVERSE_WRIST_CURL("Dumbbell Reverse Wrist Curl", ExerciseCategory.FOREARMS),
    BARBELL_REVERSE_WRIST_CURL("Barbell Reverse Wrist Curl", ExerciseCategory.FOREARMS),
    CABLE_REVERSE_WRIST_CURL("Cable Reverse Wrist Curl", ExerciseCategory.FOREARMS),
    REVERSE_CURL("Reverse Curl", ExerciseCategory.FOREARMS),
    FARMERS_CARRY("Farmer's Carry", ExerciseCategory.FOREARMS),
    SUITCASE_CARRY("Suitcase Carry", ExerciseCategory.FOREARMS),
    PLATE_PINCH_HOLD("Plate Pinch Hold", ExerciseCategory.FOREARMS),
    DEAD_HANG("Dead Hang", ExerciseCategory.FOREARMS),
    FAT_GRIP_DUMBBELL_HOLD("Fat Grip Dumbbell Hold", ExerciseCategory.FOREARMS),
    WRIST_ROLLER("Wrist Roller", ExerciseCategory.FOREARMS),
    PRONATION_SUPINATION("Pronation/Supination", ExerciseCategory.FOREARMS),
    DUMBBELL_PRONATION("Dumbbell Pronation", ExerciseCategory.FOREARMS),
    DUMBBELL_SUPINATION("Dumbbell Supination", ExerciseCategory.FOREARMS),
    RADIAL_DEVIATION("Radial Deviation", ExerciseCategory.FOREARMS),
    ULNAR_DEVIATION("Ulnar Deviation", ExerciseCategory.FOREARMS),

    // Core exercises
    PLANK("Plank", ExerciseCategory.CORE),
    SIDE_PLANK("Side Plank", ExerciseCategory.CORE),
    CRUNCH("Crunch", ExerciseCategory.CORE),
    CRUNCH_MACHINE("Crunch Machine", ExerciseCategory.CORE),
    CABLE_CRUNCH("Cable Crunch", ExerciseCategory.CORE),
    LEG_RAISE("Leg Raise", ExerciseCategory.CORE),
    BUTTER_FLY_KICKS("Butter Fly Kicks", ExerciseCategory.CORE),
    SCISSOR_KICKS("Scissor Kicks", ExerciseCategory.CORE),
    RUSSIAN_TWIST("Russian Twist", ExerciseCategory.CORE),
    AB_WHEEL_ROLLOUT("Ab Wheel Rollout", ExerciseCategory.CORE),
    DEAD_BUG("Dead Bug", ExerciseCategory.CORE),
    HANGING_LEG_RAISE("Hanging Leg Raise", ExerciseCategory.CORE),
    AB_CIRCUT("Ab Circuit", ExerciseCategory.CORE),

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

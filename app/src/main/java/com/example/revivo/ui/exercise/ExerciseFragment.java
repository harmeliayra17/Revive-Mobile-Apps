package com.example.revivo.ui.exercise;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revivo.R;
import com.example.revivo.data.ExerciseRepository;
import com.example.revivo.data.networking.network.ApiConfig;
import com.example.revivo.data.networking.response.Exercise;
import com.example.revivo.ui.exercise.detail.ExerciseDetailActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseFragment extends Fragment {
    private static final String TAG = "ExerciseFragment";

    private ExerciseViewModel viewModel;
    private RecyclerView rvExercises;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private EditText etSearch;
    private ImageView ivSettings;

    // Filter tabs
    private TextView tabAll, tabBeginner, tabIntermediate, tabExpert;
    private String currentLevelFilter = "All";

    // Summary TextViews
    private TextView tvSummaryExerciseCount;
    private TextView tvSummaryMuscleGroupsCount;
    private TextView tvSummaryLevelCount;

    // Filter variables
    private List<Exercise> originalExercises = new ArrayList<>();
    private List<Exercise> filteredExercises = new ArrayList<>();
    private ExerciseAdapter adapter;

    // Filter options
    private Set<String> selectedPrimaryMuscles = new HashSet<>();
    private Set<String> selectedEquipment = new HashSet<>();
    private Set<String> selectedForceTypes = new HashSet<>();
    private Set<String> selectedMechanics = new HashSet<>();
    private Set<String> selectedCategories = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        setupSearchFunctionality();
        setupFilterButton();
        setupFilterTabs();
        observeData();
    }

    private void initializeViews(View view) {
        // Initialize views
        rvExercises = view.findViewById(R.id.recyclerViewWorkouts);
        progressBar = view.findViewById(R.id.progress_exercise);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        etSearch = view.findViewById(R.id.etSearch);
        ivSettings = view.findViewById(R.id.ivSettings);

        // Filter tabs
        tabAll = view.findViewById(R.id.tabAll);
        tabBeginner = view.findViewById(R.id.tabBeginner);
        tabIntermediate = view.findViewById(R.id.tabIntermediate);
        tabExpert = view.findViewById(R.id.tabExpert);

        // Summary TextViews
        tvSummaryExerciseCount = view.findViewById(R.id.tv_summary_exercise_count);
        tvSummaryMuscleGroupsCount = view.findViewById(R.id.tv_summary_musclegroups_count);
        tvSummaryLevelCount = view.findViewById(R.id.tv_summary_level_count);
    }

    private void setupFilterTabs() {
        tabAll.setOnClickListener(v -> selectLevelTab("All", tabAll));
        tabBeginner.setOnClickListener(v -> selectLevelTab("beginner", tabBeginner));
        tabIntermediate.setOnClickListener(v -> selectLevelTab("intermediate", tabIntermediate));
        tabExpert.setOnClickListener(v -> selectLevelTab("expert", tabExpert));
    }

    private void selectLevelTab(String level, TextView selectedTab) {
        // Reset all tabs to unselected state
        resetTabAppearance(tabAll);
        resetTabAppearance(tabBeginner);
        resetTabAppearance(tabIntermediate);
        resetTabAppearance(tabExpert);

        // Set selected tab appearance
        setSelectedTabAppearance(selectedTab);

        // Update current filter and apply
        currentLevelFilter = level;
        applyAllFilters();
    }

    private void resetTabAppearance(TextView tab) {
        tab.setBackgroundResource(R.drawable.unselected_tab_background);
        tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
    }

    private void setSelectedTabAppearance(TextView tab) {
        tab.setBackgroundResource(R.drawable.selected_tab_background);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_tab_text_color));
    }

    private void setupRecyclerView() {
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter setup with click listener
        adapter = new ExerciseAdapter(exercise -> {
            Log.d(TAG, "Exercise clicked: " + exercise.getName());
            // Navigate to detail activity
            Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
            intent.putExtra("exercise_id", exercise.getId());
            intent.putExtra("exercise_name", exercise.getName());
            // Add other exercise data as needed
            startActivity(intent);
        });
        rvExercises.setAdapter(adapter);
    }

    private void setupViewModel() {
        // ViewModel + Repository setup
        ExerciseRepository repo = new ExerciseRepository(ApiConfig.getApiService());
        ExerciseViewModelFactory factory = new ExerciseViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(ExerciseViewModel.class);
    }

    private void setupSearchFunctionality() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButton() {
        ivSettings.setOnClickListener(v -> showFilterDialog());
        updateFilterButtonAppearance();
    }

    private void updateFilterButtonAppearance() {
        // Check if any filters are applied
        boolean hasActiveFilters = !selectedPrimaryMuscles.isEmpty() ||
                !selectedEquipment.isEmpty() ||
                !selectedForceTypes.isEmpty() ||
                !selectedMechanics.isEmpty() ||
                !selectedCategories.isEmpty() ||
                !currentLevelFilter.equals("All");

        if (hasActiveFilters) {
            ivSettings.setBackgroundResource(R.drawable.filter_active_background);
        } else {
            ivSettings.setBackground(null);
        }
    }

    private void observeData() {
        // Observe data changes
        viewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            Log.d(TAG, "Exercises LiveData changed: " +
                    (exercises != null ? exercises.size() + " items" : "null"));

            if (exercises != null && !exercises.isEmpty()) {
                originalExercises = new ArrayList<>(exercises);
                applyAllFilters();
                tvErrorMessage.setVisibility(View.GONE);
                rvExercises.setVisibility(View.VISIBLE);
            } else {
                tvErrorMessage.setText("No exercise data found.");
                tvErrorMessage.setVisibility(View.VISIBLE);
                rvExercises.setVisibility(View.GONE);
                clearSummary();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null && !err.isEmpty()) {
                String errorMessage = "An error occurred while loading data.";
                if (err.contains("Unable to resolve host")) {
                    errorMessage = "No internet connection.";
                } else if (err.contains("Failed to connect") || err.contains("timeout")) {
                    errorMessage = "Failed to connect to the server.";
                } else if (err.contains("HTTP")) {
                    errorMessage = "Server error: " + err;
                }
                tvErrorMessage.setText(errorMessage);
                tvErrorMessage.setVisibility(View.VISIBLE);
                rvExercises.setVisibility(View.GONE);
                clearSummary();
            } else {
                tvErrorMessage.setVisibility(View.GONE);
            }
        });
    }

    private void filterExercises(String searchQuery) {
        applyAllFilters(searchQuery);
    }

    private void applyAllFilters() {
        applyAllFilters(etSearch.getText().toString());
    }

    private void applyAllFilters(String searchQuery) {
        List<Exercise> result = new ArrayList<>(originalExercises);

        // Apply level filter from tabs
        if (!currentLevelFilter.equals("All")) {
            List<Exercise> levelFiltered = new ArrayList<>();
            for (Exercise exercise : result) {
                if (exercise.getLevel() != null &&
                        exercise.getLevel().toLowerCase().equals(currentLevelFilter.toLowerCase())) {
                    levelFiltered.add(exercise);
                }
            }
            result = levelFiltered;
        }

        // Apply search filter
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            List<Exercise> searchResult = new ArrayList<>();
            String query = searchQuery.toLowerCase().trim();
            for (Exercise exercise : result) {
                if (exercise.getName() != null &&
                        exercise.getName().toLowerCase().contains(query)) {
                    searchResult.add(exercise);
                }
            }
            result = searchResult;
        }

        // Apply category filters
        result = applyFilters(result);

        filteredExercises = result;
        adapter.submitList(new ArrayList<>(filteredExercises));
        updateSummary(filteredExercises);
        updateFilterButtonAppearance();
    }

    private List<Exercise> applyFilters(List<Exercise> exercises) {
        List<Exercise> result = new ArrayList<>();

        for (Exercise exercise : exercises) {
            boolean matchesFilters = true;

            // Primary Muscle filter
            if (!selectedPrimaryMuscles.isEmpty()) {
                boolean matchesPrimaryMuscle = false;
                if (exercise.getPrimaryMuscles() != null) {
                    String cleanMuscleString = cleanPrimaryMuscleString(exercise.getPrimaryMuscles().toString());
                    for (String selectedMuscle : selectedPrimaryMuscles) {
                        if (cleanMuscleString.toLowerCase().contains(selectedMuscle.toLowerCase())) {
                            matchesPrimaryMuscle = true;
                            break;
                        }
                    }
                }
                if (!matchesPrimaryMuscle) matchesFilters = false;
            }

            // Equipment filter
            if (!selectedEquipment.isEmpty() && matchesFilters) {
                boolean matchesEquipment = false;
                if (exercise.getEquipment() != null) {
                    for (String selectedEq : selectedEquipment) {
                        if (exercise.getEquipment().toLowerCase()
                                .contains(selectedEq.toLowerCase())) {
                            matchesEquipment = true;
                            break;
                        }
                    }
                }
                if (!matchesEquipment) matchesFilters = false;
            }

            // Force Type filter
            if (!selectedForceTypes.isEmpty() && matchesFilters) {
                boolean matchesForce = false;
                if (exercise.getForce() != null) {
                    for (String selectedForce : selectedForceTypes) {
                        if (exercise.getForce().toLowerCase()
                                .contains(selectedForce.toLowerCase())) {
                            matchesForce = true;
                            break;
                        }
                    }
                }
                if (!matchesForce) matchesFilters = false;
            }

            // Mechanic filter
            if (!selectedMechanics.isEmpty() && matchesFilters) {
                boolean matchesMechanic = false;
                if (exercise.getMechanic() != null) {
                    for (String selectedMech : selectedMechanics) {
                        if (exercise.getMechanic().toLowerCase()
                                .contains(selectedMech.toLowerCase())) {
                            matchesMechanic = true;
                            break;
                        }
                    }
                }
                if (!matchesMechanic) matchesFilters = false;
            }

            // Category filter
            if (!selectedCategories.isEmpty() && matchesFilters) {
                boolean matchesCategory = false;
                if (exercise.getCategory() != null) {
                    for (String selectedCat : selectedCategories) {
                        if (exercise.getCategory().toLowerCase()
                                .contains(selectedCat.toLowerCase())) {
                            matchesCategory = true;
                            break;
                        }
                    }
                }
                if (!matchesCategory) matchesFilters = false;
            }

            if (matchesFilters) {
                result.add(exercise);
            }
        }

        return result;
    }

    private String cleanPrimaryMuscleString(String muscleString) {
        // Remove brackets and quotes from primary muscle string
        // e.g., ["quadriceps"] -> quadriceps
        return muscleString.replaceAll("[\\[\\]\"']", "").trim();
    }

    private void styleAndSizeDialog(AlertDialog dialog, float widthPercent, float heightPercent) {
        Window window = dialog.getWindow();
        if (window == null) return;

        // Ambil ukuran layar
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth  = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Hitung ukuran dialog
        int dialogWidth  = (int) (screenWidth  * widthPercent);
        int dialogHeight = (int) (screenHeight * heightPercent);

        // Set ukuran dialog
        WindowManager.LayoutParams params = window.getAttributes();
        params.width  = dialogWidth;
        params.height = dialogHeight;
        window.setAttributes(params);

        // Buat background dengan sudut membulat
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE); // Warna background dialog
        float radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, requireContext().getResources().getDisplayMetrics()
        );
        background.setCornerRadius(radius);

        // Terapkan background ke dialog
        window.setBackgroundDrawable(background);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Get screen dimensions for proper sizing
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        // Create the main container with rounded corners
        LinearLayout dialogContainer = new LinearLayout(getContext());
        dialogContainer.setOrientation(LinearLayout.VERTICAL);
        dialogContainer.setBackgroundResource(android.R.color.white);

        // Apply rounded corners programmatically
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(60f);
        background.setColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        dialogContainer.setBackground(background);

        // Create custom title with close button
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(40, 30, 40, 20);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        TextView titleView = new TextView(getContext());
        titleView.setText("Filter Exercises");
        titleView.setTextSize(20);
        titleView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        titleView.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        titleView.setLayoutParams(titleParams);

        // Close button (X icon)
        ImageView closeButton = new ImageView(getContext());
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
        closeButton.setPadding(10, 10, 10, 10);
        closeButton.setClickable(true);
        closeButton.setFocusable(true);

        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        closeButton.setLayoutParams(closeParams);

        titleLayout.addView(titleView);
        titleLayout.addView(closeButton);

        // Create main scroll view with proper height constraints
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        scrollView.setFillViewport(true);

        // Set scroll view height to allow space for buttons
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(scrollParams);

        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 0, 30, 20);
        mainLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Get unique values from current exercises for filter options
        Set<String> availablePrimaryMuscles = getUniquePrimaryMuscles();
        Set<String> availableEquipment = getUniqueEquipment();
        Set<String> availableForceTypes = getUniqueForceTypes();
        Set<String> availableMechanics = getUniqueMechanics();
        Set<String> availableCategories = getUniqueCategories();

        // Add filter sections with toggle button styling
        addToggleFilterSection(mainLayout, "Primary Muscles", availablePrimaryMuscles, selectedPrimaryMuscles);
        addSpacer(mainLayout);

        addToggleFilterSection(mainLayout, "Equipment", availableEquipment, selectedEquipment);
        addSpacer(mainLayout);

        addToggleFilterSection(mainLayout, "Category", availableCategories, selectedCategories);
        addSpacer(mainLayout);

        addToggleFilterSection(mainLayout, "Force Type", availableForceTypes, selectedForceTypes);
        addSpacer(mainLayout);

        addToggleFilterSection(mainLayout, "Mechanic", availableMechanics, selectedMechanics);

        // Add bottom padding to ensure content doesn't get cut off
        View bottomPadding = new View(getContext());
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 30);
        bottomPadding.setLayoutParams(bottomParams);
        mainLayout.addView(bottomPadding);

        scrollView.addView(mainLayout);

        // Create button layout with fixed height
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(30, 20, 30, 30);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Set fixed height for button layout
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayout.setLayoutParams(buttonLayoutParams);


        // Clear All button (left)
        Button clearAllButton = new Button(getContext());
        clearAllButton.setText("Clear All");
        clearAllButton.setTextSize(14);
        clearAllButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        clearParams.setMargins(0, 0, 10, 0);
        clearAllButton.setLayoutParams(clearParams);

        GradientDrawable clearBackground = new GradientDrawable();
        clearBackground.setShape(GradientDrawable.RECTANGLE);
        clearBackground.setCornerRadius(8f);
        clearBackground.setColor(ContextCompat.getColor(requireContext(), R.color.inactive_grey));
        clearAllButton.setBackground(clearBackground);

        // Apply button (right)
        Button applyButton = new Button(getContext());
        applyButton.setText("Apply");
        applyButton.setTextSize(14);
        applyButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Create custom background for apply button
        GradientDrawable applyBackground = new GradientDrawable();
        applyBackground.setShape(GradientDrawable.RECTANGLE);
        applyBackground.setCornerRadius(8f);
        applyBackground.setColor(ContextCompat.getColor(requireContext(), R.color.selected_tab_text_color));
        applyButton.setBackground(applyBackground);

        LinearLayout.LayoutParams applyParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        applyParams.setMargins(10, 0, 0, 0);
        applyButton.setLayoutParams(applyParams);

        buttonLayout.addView(clearAllButton);
        buttonLayout.addView(applyButton);

        // Add all components to dialog container
        dialogContainer.addView(titleLayout);
        dialogContainer.addView(scrollView);
        dialogContainer.addView(buttonLayout);

        builder.setView(dialogContainer);

        AlertDialog dialog = builder.create();


        // Set button click listeners
        closeButton.setOnClickListener(v -> dialog.dismiss());


        clearAllButton.setOnClickListener(v -> {
            clearAllFilters();
            applyAllFilters();
            dialog.dismiss();
        });

        applyButton.setOnClickListener(v -> {
            applyAllFilters();
            dialog.dismiss();
        });

        dialog.show();
        styleAndSizeDialog(dialog, 0.9f, 0.75f);
    }

    private void addSpacer(LinearLayout parent) {
        View spacer = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 30);
        spacer.setLayoutParams(params);
        parent.addView(spacer);
    }

    private void addToggleFilterSection(LinearLayout parent, String title, Set<String> options, Set<String> selectedOptions) {
        // Section title with emoji and count
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText(title + " (" + options.size() + ")");
        sectionTitle.setTextSize(16);
        sectionTitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        sectionTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        sectionTitle.setPadding(0, 0, 0, 15);
        parent.addView(sectionTitle);

        // Create a container for toggle buttons with flexible layout
        LinearLayout toggleContainer = new LinearLayout(getContext());
        toggleContainer.setOrientation(LinearLayout.VERTICAL);

        // Create rows for toggle buttons (2-3 buttons per row)
        LinearLayout currentRow = null;
        int itemsInRow = 0;
        int maxItemsPerRow = 2; // Adjust based on your preference

        for (String option : options) {
            // Create new row if needed
            if (currentRow == null || itemsInRow >= maxItemsPerRow) {
                currentRow = new LinearLayout(getContext());
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setPadding(0, 5, 0, 5);
                toggleContainer.addView(currentRow);
                itemsInRow = 0;
            }

            // Create toggle button
            TextView toggleButton = createToggleButton(option, selectedOptions.contains(option));

            // Set click listener
            toggleButton.setOnClickListener(v -> {
                boolean isSelected = selectedOptions.contains(option);
                if (isSelected) {
                    selectedOptions.remove(option);
                    setToggleButtonUnselected(toggleButton);
                } else {
                    selectedOptions.add(option);
                    setToggleButtonSelected(toggleButton);
                }
            });

            // Add to current row with margin
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            buttonParams.setMargins(0, 0, 10, 10);
            toggleButton.setLayoutParams(buttonParams);

            currentRow.addView(toggleButton);
            itemsInRow++;
        }

        parent.addView(toggleContainer);
    }

    private TextView createToggleButton(String text, boolean isSelected) {
        TextView button = new TextView(getContext());
        button.setText(text);
        button.setTextSize(12);
        button.setPadding(20, 15, 20, 15);
        button.setGravity(android.view.Gravity.CENTER);
        button.setClickable(true);
        button.setFocusable(true);

        // Set initial state
        if (isSelected) {
            setToggleButtonSelected(button);
        } else {
            setToggleButtonUnselected(button);
        }

        return button;
    }

    private void setToggleButtonSelected(TextView button) {
        // Create rounded background programmatically
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        background.setCornerRadius(25f);
        background.setColor(ContextCompat.getColor(requireContext(), R.color.selected_tab_text_color));

        button.setBackground(background);
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        button.setTypeface(null, android.graphics.Typeface.BOLD);

        // Add elevation effect
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(4f);
        }
    }

    private void setToggleButtonUnselected(TextView button) {
        // Create rounded border background
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        background.setCornerRadius(25f);
        background.setColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
        background.setStroke(2, ContextCompat.getColor(requireContext(), android.R.color.darker_gray));

        button.setBackground(background);
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
        button.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Remove elevation
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(0f);
        }
    }

    private Set<String> getUniquePrimaryMuscles() {
        Set<String> muscles = new HashSet<>();
        for (Exercise exercise : originalExercises) {
            if (exercise.getPrimaryMuscles() != null) {
                // Clean the muscle string by removing brackets and quotes
                String cleanMuscle = cleanPrimaryMuscleString(exercise.getPrimaryMuscles().toString());
                if (!cleanMuscle.isEmpty()) {
                    muscles.add(cleanMuscle);
                }
            }
        }
        return muscles;
    }

    private Set<String> getUniqueEquipment() {
        Set<String> equipment = new HashSet<>();
        for (Exercise exercise : originalExercises) {
            if (exercise.getEquipment() != null && !exercise.getEquipment().isEmpty()) {
                equipment.add(exercise.getEquipment());
            }
        }
        return equipment;
    }

    private Set<String> getUniqueForceTypes() {
        Set<String> forceTypes = new HashSet<>();
        for (Exercise exercise : originalExercises) {
            if (exercise.getForce() != null && !exercise.getForce().isEmpty()) {
                forceTypes.add(exercise.getForce());
            }
        }
        return forceTypes;
    }

    private Set<String> getUniqueMechanics() {
        Set<String> mechanics = new HashSet<>();
        for (Exercise exercise : originalExercises) {
            if (exercise.getMechanic() != null && !exercise.getMechanic().isEmpty()) {
                mechanics.add(exercise.getMechanic());
            }
        }
        return mechanics;
    }

    private Set<String> getUniqueCategories() {
        Set<String> categories = new HashSet<>();
        for (Exercise exercise : originalExercises) {
            if (exercise.getCategory() != null && !exercise.getCategory().isEmpty()) {
                categories.add(exercise.getCategory());
            }
        }
        return categories;
    }

    private void clearAllFilters() {
        selectedPrimaryMuscles.clear();
        selectedEquipment.clear();
        selectedForceTypes.clear();
        selectedMechanics.clear();
        selectedCategories.clear();
        // Reset level filter to "All"
        currentLevelFilter = "All";
        selectLevelTab("All", tabAll);
    }

    private void updateSummary(List<Exercise> exercises) {
        // Total exercises count
        int totalExercise = exercises.size();

        // Find unique muscle groups
        Set<String> uniqueMuscleGroups = new HashSet<>();
        // Find unique levels
        Set<String> uniqueLevels = new HashSet<>();

        for (Exercise e : exercises) {
            if (e.getPrimaryMuscles() != null) {
                String cleanMuscle = cleanPrimaryMuscleString(e.getPrimaryMuscles().toString());
                uniqueMuscleGroups.add(cleanMuscle);
            }
            if (e.getLevel() != null) {
                uniqueLevels.add(e.getLevel());
            }
        }

        // Update summary UI
        tvSummaryExerciseCount.setText(String.valueOf(totalExercise));
        tvSummaryMuscleGroupsCount.setText(String.valueOf(uniqueMuscleGroups.size()));
        tvSummaryLevelCount.setText(String.valueOf(uniqueLevels.size()));
    }

    private void clearSummary() {
        tvSummaryExerciseCount.setText("0");
        tvSummaryMuscleGroupsCount.setText("0");
        tvSummaryLevelCount.setText("0");
    }
}
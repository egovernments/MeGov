package org.egovernments.egoverp.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.NothingSelectedSpinnerAdapter;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("unchecked")
public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String phoneno;
    private String email;
    private String password;
    private String confirmpassword;
    private String deviceID;
    private String deviceOS;
    private String deviceType;

    private TextView municipalityInfo;

    private TextInputLayout nameInputLayout;

    private String url;
    private String cityName;

    private ProgressDialog progressDialog;

    private ConfigManager configManager;

    private Handler handler;

    private Spinner spinnerCity;
    private Spinner spinnerDistrict;

    private SessionManager sessionManager;

    private CustomAutoCompleteTextView cityAutoCompleteTextView;
    private CustomAutoCompleteTextView districtAutoCompleteTextView;

    List<District> districtsList;
    List<City> citiesList;

    private int check = 0;

    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinnerCity = (Spinner) findViewById(R.id.signup_city);
        spinnerDistrict =(Spinner) findViewById(R.id.spinner_district);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing request");
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(this);

        municipalityInfo = (TextView) findViewById(R.id.municipality_change_info);
        nameInputLayout = (TextInputLayout) findViewById(R.id.signup_name_inputlayout);

        final EditText name_edittext = (EditText) findViewById(R.id.signup_name);
        final EditText email_edittext = (EditText) findViewById(R.id.signup_email);
        final EditText phoneno_edittext = (EditText) findViewById(R.id.signup_phoneno);
        final EditText password_edittext = (EditText) findViewById(R.id.signup_password);
        final EditText confirmpassword_edittext = (EditText) findViewById(R.id.signup_confirmpassword);

        cityAutoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.register_spinner_autocomplete);
        districtAutoCompleteTextView =(CustomAutoCompleteTextView)findViewById(R.id.autocomplete_district);

        cityAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Fetching municipality list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        districtAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Fetching districts list, please wait", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceOS = Integer.toString(Build.VERSION.SDK_INT);
        deviceType = "mobile";


        Button register = (Button) findViewById(R.id.signup_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                name = name_edittext.getText().toString().trim();
                email = email_edittext.getText().toString().trim();
                phoneno = phoneno_edittext.getText().toString().trim();
                password = password_edittext.getText().toString().trim();
                confirmpassword = confirmpassword_edittext.getText().toString().trim();
                submit(name, email, phoneno, password, confirmpassword);
            }
        });

        confirmpassword_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    progressDialog.show();

                    name = name_edittext.getText().toString().trim();
                    email = email_edittext.getText().toString().trim();
                    phoneno = phoneno_edittext.getText().toString().trim();
                    password = password_edittext.getText().toString().trim();
                    confirmpassword = confirmpassword_edittext.getText().toString().trim();
                    submit(name, email, phoneno, password, confirmpassword);
                    return true;
                }
                return false;
            }
        });

        handler = new Handler();

        try {
            InputStream inputStream = getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, RegisterActivity.this);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GetAllCitiesTask().execute();

    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String expression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$;:+=-_?()!]).{8,32})";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void submit(final String name, final String email, final String phoneno, final String password, final String confirmpassword) {

        City selectedCity=getCityByName(cityAutoCompleteTextView.getText().toString());

        if (selectedCity == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneno) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpassword)) {
            Toast toast = Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressDialog.dismiss();
        } else if (phoneno.length() != 10) {
            Toast toast = Toast.makeText(RegisterActivity.this, "Phone no. must be 10 digits", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressDialog.dismiss();
        } else if (!isValidEmail(email)) {
            Toast toast = Toast.makeText(RegisterActivity.this, "Please enter a valid email ID", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressDialog.dismiss();
        } else if (!isValidPassword(password)) {
            Toast toast = Toast.makeText(RegisterActivity.this, "Password must be 8-32 characters long, containing at least one uppercase letter, one lowercase letter, and one number or special character excluding '& < > # % \" ' / \\' and space", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressDialog.dismiss();
        } else if (!password.equals(confirmpassword)) {
            Toast toast = Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressDialog.dismiss();
        } else {

            sessionManager.setBaseURL(selectedCity.getUrl(), selectedCity.getCityName(), selectedCity.getCityCode());
            RegisterRequest registerRequest = new RegisterRequest(email, phoneno, name, password, deviceID, deviceType, deviceOS);
            ApiController.resetAndGetAPI(RegisterActivity.this).registerUser(registerRequest, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast toast = Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            progressDialog.dismiss();

                        }
                    });


                    ApiController.getAPI(RegisterActivity.this).sendOTP(phoneno, new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });


                    Intent intent = new Intent(RegisterActivity.this, AccountActivationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", phoneno);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }


                @Override
                public void failure(final RetrofitError error) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            JsonObject jsonObject = null;

                            progressDialog.dismiss();

                            if (error != null) {
                                if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().contains("400")) {
                                    Toast toast = Toast.makeText(RegisterActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    try {
                                        jsonObject = (JsonObject) error.getBody();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (jsonObject != null) {
                                        Toast toast = Toast.makeText(RegisterActivity.this, "An account already exists with that email ID or mobile no.", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    } else {
                                        Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }

                                }
                            } else {
                                Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while accessing the network", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }
                    });
                }
            });
        }
    }

    class GetAllCitiesTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {

            loadDropdowns();

           /* try {
                if (configManager.getString("api.multicities").equals("false")) {

                } else {
                    *//*final List<City> cityList = ApiController.getAllCitiesURLs(configManager.getString("api.multipleCitiesUrl"));
                    if (cityList != null) {
                        final List<String> cities = new ArrayList<>();

                        for (int i = 0; i < cityList.size(); i++) {
                            cities.add(cityList.get(i).getCityName());
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCity.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, RegisterActivity.this));
                                spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        check = check + 1;
                                        if (check > 1) {
                                            url = cityList.get(position - 1).getUrl();
                                            cityName = cityList.get(position - 1).getCityName();
                                            code = cityList.get(position - 1).getCityCode();
                                            cityAutoCompleteTextView.setText(cityList.get(position - 1).getCityName());
                                            cityAutoCompleteTextView.dismissDropDown();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                                cityAutoCompleteTextView.setHint("Municipality");
                                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                                cityAutoCompleteTextView.setOnClickListener(null);
                                cityAutoCompleteTextView.setAdapter(autoCompleteAdapter);
                                cityAutoCompleteTextView.setThreshold(1);
                                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                    @Override
                                    public void onClick(DrawablePosition target) {
                                        if (target == DrawablePosition.RIGHT) {
                                            spinnerCity.performClick();
                                        }
                                    }
                                });
                                cityAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        String s = cityAutoCompleteTextView.getText().toString();
                                        for (City city : cityList) {
                                            if (s.equals(city.getCityName())) {
                                                url = city.getUrl();
                                                cityName = city.getCityName();
                                                code = city.getCityCode();
                                            }

                                        }
                                    }
                                });

                            }
                        });

                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                cityAutoCompleteTextView.setOnClickListener(null);
                                cityAutoCompleteTextView.setHint("Loading failed");
                                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                    @Override
                                    public void onClick(DrawablePosition target) {
                                        if (target == DrawablePosition.RIGHT) {
                                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                                            cityAutoCompleteTextView.setDrawableClickListener(null);
                                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                                            new GetAllCitiesTask().execute();
                                        }
                                    }
                                });
                                Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while retrieving the list of available municipalities", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                                    @Override
                                    public void onClick(DrawablePosition target) {
                                        if (target == DrawablePosition.RIGHT) {
                                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                                            cityAutoCompleteTextView.setDrawableClickListener(null);
                                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                                            new GetAllCitiesTask().execute();
                                        }
                                    }
                                });
                            }
                        });
                    }*//*
                }
            } catch (Exception e) {
                e.printStackTrace();

            }*/

            return null;
        }
    }

    public void resetAndRefreshDropdown()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cityAutoCompleteTextView.setOnClickListener(null);
                cityAutoCompleteTextView.setHint("Loading failed");
                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });
                Toast toast = Toast.makeText(RegisterActivity.this, "An unexpected error occurred while retrieving the list of available municipalities", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });
            }
        });
    }


    public void loadDropdowns()
    {
        try {

            if (configManager.getString("api.multicities").equals("false")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        municipalityInfo.setVisibility(View.GONE);
                        spinnerCity.setVisibility(View.GONE);
                        cityAutoCompleteTextView.setVisibility(View.GONE);
                        nameInputLayout.setBackgroundResource(R.drawable.top_edittext);
                    }
                });
            } else {
                loadDistrictDropdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetAndRefreshDropdown();
        }
    }

    public void loadDistrictDropdown() throws IOException
    {
        districtsList = ApiController.getAllCitiesURLs(configManager.getString("api.multipleCitiesUrl"));

        if (districtsList != null) {

            final List<String> districts = new ArrayList<>();

            for (int i = 0; i < districtsList.size(); i++) {
                districts.add(districtsList.get(i).getDistrictName());
            }

            loadDropdownsWithData(districts, spinnerDistrict, districtAutoCompleteTextView, true);

        } else {
            resetAndRefreshDropdown();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCityDropdown()
    {
        if(citiesList!=null){ citiesList.clear(); }

        cityAutoCompleteTextView.setHint("Loading");
        cityAutoCompleteTextView.setOnClickListener(null);
        cityAutoCompleteTextView.setAdapter(null);

        spinnerCity.setAdapter(null);

        String s = districtAutoCompleteTextView.getText().toString().toUpperCase();
        List<String> cities=new ArrayList<>();

        for (District district : districtsList) {
            if (s.equals(district.getDistrictName().toUpperCase())) {
                districtAutoCompleteTextView.setText(s.toUpperCase());
                cityAutoCompleteTextView.requestFocus();
                //noinspection unchecked
                citiesList= (List<City>) ((ArrayList<City>)district.getCities()).clone();
                for(City city: citiesList)
                {
                    cities.add(city.getCityName());
                }
                break;
            }
        }

        loadDropdownsWithData(cities, spinnerCity, cityAutoCompleteTextView, false);
    }

    public void loadDropdownsWithData(final List<String> autocompleteList, final Spinner autoCompleteSpinner, final CustomAutoCompleteTextView autocompleteTextBox, final Boolean isDistrict)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoCompleteSpinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, RegisterActivity.this));
                autoCompleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if ((position - 1) > -1) {
                            if (isDistrict) {
                                autocompleteTextBox.setText(districtsList.get(position - 1).getDistrictName());
                                loadCityDropdown();
                            } else {
                                City selectedCity = citiesList.get(position - 1);
                                autocompleteTextBox.setText(selectedCity.getCityName());
                            }
                        }
                        autocompleteTextBox.dismissDropDown();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                //String hintText = (autocompleteList.size() > 0 ? (isDistrict ? "District" : "Municipality") : (isDistrict ? "Districts Not Found!" : "Municipalities Not Found!"));

                autocompleteTextBox.setHint((isDistrict ? "District" : "Municipality"));
                autocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds((isDistrict ? R.drawable.ic_place_black_24dp : R.drawable.ic_location_city_black_24dp), 0, (autocompleteList.size() > 0 ? R.drawable.ic_keyboard_arrow_down_black_24dp : 0), 0);
                autocompleteTextBox.setOnClickListener(null);
                autocompleteTextBox.setAdapter(autoCompleteAdapter);
                autocompleteTextBox.setThreshold(1);
                autocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            if (!isDistrict && ! cityAutoCompleteTextView.hasFocus()) {
                                return;
                            }
                            autoCompleteSpinner.performClick();
                        }
                    }
                });

                autocompleteTextBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (isDistrict) {
                            if (citiesList != null) {
                                citiesList.clear();
                            }
                            cityAutoCompleteTextView.setText("");
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                autocompleteTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View v, boolean hasFocus) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!v.hasFocus()) {
                                    if (isDistrict && !TextUtils.isEmpty(districtAutoCompleteTextView.getText().toString())) {
                                        loadCityDropdown();
                                    }
                                }
                            }
                        });

                    }
                });

                autocompleteTextBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (isDistrict) {
                            loadCityDropdown();
                        }
                    }
                });

            }
        });
    }


    public City getCityByName(String cityName)
    {
        if(citiesList!=null) {
            for (City city : citiesList) {
                if (cityName.equals(city.getCityName())) {
                    return city;
                }
            }
        }
        return null;
    }




}



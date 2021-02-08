package gonevas.com.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gonevas.com.R;
import gonevas.com.adapter.SearchResultsAdapter;
import gonevas.com.db.DatabaseRepository;
import gonevas.com.model.SearchResults;
import gonevas.com.utils.Tools;

public class SearchFormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_search;
    private ImageButton bt_clear;


    RecyclerView search_suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);
        context = this;

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        search_suggestions = findViewById(R.id.search_suggestions);


        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(textWatcher);

        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);


        showSuggestionSearch();

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                et_search.append("");
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    key_word = et_search.getText().toString();
                    if (key_word.length() < 2) {
                        Toast.makeText(context, "Keyword too short.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSuggestionSearch();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });
    }

    DatabaseRepository databaseRepository;
    Context context;

    SearchResultsAdapter searchResultsAdapter;
    List<String> searchResults = new ArrayList<>();

    private void showSuggestionSearch() {
        databaseRepository = new DatabaseRepository(this);
        databaseRepository.get_search_results().observe((LifecycleOwner) context, new Observer<List<SearchResults>>() {
            @Override
            public void onChanged(List<SearchResults> dbSearchResults) {
                if (dbSearchResults.size() > 0) {


                    if (!dbSearchResults.isEmpty()) {

                        for (SearchResults search : dbSearchResults) {
                            if (!searchResults.contains(search.keyword.toLowerCase())) {
                                searchResults.add(search.keyword.toLowerCase());
                            }
                        }


                        searchResultsAdapter = new SearchResultsAdapter(context, searchResults, 1);
                        search_suggestions.setHasFixedSize(true);
                        search_suggestions.setLayoutManager(new LinearLayoutManager(context));
                        search_suggestions.setAdapter(searchResultsAdapter);

                        searchResultsAdapter.setOnItemClickListener(new SearchResultsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, String obj, int position) {
                                et_search.setText(obj);
                                et_search.append("");
                                searchAction();
                            }
                        });

                    }

                } else {

                }
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    String key_word = "";

    private void searchAction() {

        key_word = et_search.getText().toString();
        if (key_word.length() < 2) {
            Toast.makeText(context, "Keyword too short.", Toast.LENGTH_SHORT).show();
            return;
        }

        hideKeyboard();
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra("search", key_word + "");
        context.startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
package edu.ucsb.ece150.gauchopay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.braintreepayments.cardform.view.CardForm;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Card");
        setSupportActionBar(toolbar);

        // Note that the requirements here are just for creating the fields on the form. For
        // example, if the cvvRequired setting was set to "false", the form would not contain
        // a field for CVV. ("Requirement" DOES NOT mean "Valid".)
        final CardForm cardForm = findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .actionLabel("Add Card")
                .setup(this);

        // [TODO] Implement a method of getting card information and sending it to the main activity.
        // You will want to add a new component onto this activity's layout so you can perform this
        // task as a result of a button click.
        //
        //  Get card information from the CardForm view. Refer to the library website
        // https://github.com/braintree/android-card-form/blob/master/README.md.
        //
        // This information has to be sent back to the CardListActivity (to update the
        // list of cards).
        Button saveCardBtn = findViewById(R.id.saveCardButton);
        saveCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    String cardNumber = cardForm.getCardNumber();

                    // Save the card to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("CardData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    String existingCards = prefs.getString("cardArray", "");
                    existingCards += cardNumber + ",";
                    editor.putString("cardArray", existingCards);
                    editor.apply();

                    // Displaying the saved card number
                    Toast.makeText(AddCardActivity.this, "Saved Card: " + cardNumber, Toast.LENGTH_SHORT).show();

                    // Return to the CardListActivity
                    finish();
                }
                else {
                    Toast.makeText(AddCardActivity.this, "Card Info is not valid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

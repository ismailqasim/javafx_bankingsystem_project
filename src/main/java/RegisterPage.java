import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Account;

public class RegisterPage {
	Firestore db;
	TextField txtFirstname;
	TextField txtLastname;
	TextField txtPhone;
	PasswordField txtPass;
	ComboBox<String> type;
	ComboBox<String> gender;
	Stage stage;
	Scene scene;

	RegisterPage() {
		db = FirestoreClient.getFirestore();
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("AIAS Banking Registration");

		VBox vBox = new VBox(10); // with 10 spacing
		vBox.setAlignment(Pos.CENTER);
		vBox.setBackground(new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY)));

		Label title = new Label("AIAS Banking Registration");
		title.setFont(new Font(24));
		title.setAlignment(Pos.CENTER);

		Label l1 = new Label("Enter your first name:");
		Label l2 = new Label("Enter your last name:");
		Label l3 = new Label("Enter your phone number:");
		Label l4 = new Label("Select account type:");
		Label l5 = new Label("Select your gender:");
		Label l6 = new Label("Enter account password:");

		txtFirstname = new TextField();
		txtLastname = new TextField();
		txtPhone = new TextField();
		txtPhone.setMaxWidth(160);

		txtPass = new PasswordField();
		txtPass.setMaxWidth(160);

		type = new ComboBox<String>();
		type.getItems().addAll("Current account", "Saving account");

		gender = new ComboBox<String>();
		gender.getItems().addAll("Male", "Female", "Not specified");

		VBox box1 = new VBox(10, l1, txtFirstname);
		VBox box2 = new VBox(10, l2, txtLastname);

		HBox hBox = new HBox(10, box1, box2);
		hBox.setAlignment(Pos.BASELINE_CENTER);

		Button btnRegister = new Button("Register now");

		vBox.getChildren().addAll(title, hBox, l3, txtPhone, l4, type, l5, gender, l6, txtPass, btnRegister);

		scene = new Scene(vBox, 400, 500);
		stage.setScene(scene);
		stage.show();

		btnRegister.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				scene.setCursor(Cursor.WAIT);
				createAccount(txtFirstname.getText(), txtLastname.getText(), txtPhone.getText(),
						type.getValue(), gender.getValue(), txtPass.getText(), 
						new Random().nextInt(99999) + 1);
			}
		});
	}

	private void createAccount(final String firstName, 
			String lastName, 
			String phone, 
			String type, 
			String gender, 
			String password,
			long accountId) {
		Account account = new Account().createAccount(accountId, firstName, lastName, type, gender, password);
		ApiFuture<WriteResult> future = db.collection("users").document(accountId+"").set(account);

		future.addListener(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Data has been updated!");
			}
		}, new Executor() {
			@Override
			public void execute(Runnable command) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						Map<String, Object> docData = new HashMap<>();
						docData.put("requests", FieldValue.increment(1));
						docData.put("users", FieldValue.increment(1));
						DocumentReference docRef = db.collection("info").document("stats");
						docRef.update(docData);
						
						scene.setCursor(Cursor.DEFAULT);
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Account registered");
						alert.setHeaderText(firstName+", your new account has been registered.");
						alert.setContentText("Please wait for an admin to approve your account so then you can login.");
						System.out.println("Data has been executed!");
						alert.setOnHidden(new EventHandler<DialogEvent>() {
							@Override
							public void handle(DialogEvent event) {
								System.out.println("Dialog closed");
								stage.close();
							}
						});
						alert.showAndWait();
						
					}
				});

			}
		});
	}
}

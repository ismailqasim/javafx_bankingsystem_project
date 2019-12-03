import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
public class LoginPage {
	Firestore db;
	TextField txtUsername;
	PasswordField txtPassword;
	Scene scene;
	Stage stage;
	
	LoginPage() {
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("AIAS Banking Login");
		
		VBox vBox = new VBox(10); // with 10 spacing
		vBox.setAlignment(Pos.CENTER);
		vBox.setBackground(new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY)));
		
		Label title = new Label("AIAS Banking System");
		title.setFont(new Font(24));
		title.setAlignment(Pos.CENTER);
		
		Label accountId = new Label("Enter your account ID:");
		txtUsername = new TextField("31838");
		txtUsername.setMaxWidth(160);
		txtPassword = new PasswordField();
		txtPassword.setText("qwerty");
		txtPassword.setMaxWidth(160);
		Label accountPassword = new Label("Enter your account password:");
		
		Button btnLogin = new Button("Login");
		Button btnLoginAdmin = new Button("Login as administrator");
		
		HBox footer = new HBox(10, btnLogin, btnLoginAdmin);
		footer.setAlignment(Pos.BASELINE_CENTER);
		
		Hyperlink registerAccount = new Hyperlink("Register an account");
		
		vBox.getChildren().addAll(title, accountId, txtUsername, accountPassword, txtPassword, footer, registerAccount);
		
		scene = new Scene(vBox, 400, 300);
		stage.setScene(scene);
		stage.show();
		
		db = FirestoreClient.getFirestore();
		btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				login(Long.parseLong(txtUsername.getText()), txtPassword.getText());
			}
		});
		
		btnLoginAdmin.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loginAdmin(Long.parseLong(txtUsername.getText()), txtPassword.getText());
			}
		});
		
		registerAccount.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new RegisterPage();
			}
		});
		
	}
	
	private void login(long accountId, String password) {
		CollectionReference users = db.collection("users");
		// Create a query against the collection.
		Query query = users.whereEqualTo("accountId", accountId).whereEqualTo("password", password);
		// retrieve  query results asynchronously using query.get()
		scene.setCursor(Cursor.WAIT);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		try {
			scene.setCursor(Cursor.DEFAULT);
			if (querySnapshot.get().getDocuments().size() > 0) {
				DocumentSnapshot data = querySnapshot.get().getDocuments().get(0);
				if (!data.contains("approved")) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Your account is not approved yet");
					alert.setContentText("Please wait for an admin to approve your account");
					alert.showAndWait();
					return;
				}
				if (!data.getBoolean("approved")) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Your account is not approved yet");
					alert.setContentText("Please wait for an admin to approve your account");
					alert.showAndWait();
				} else {
					// login successful
					System.out.println("Login successful");
					new Dashboard(data);
					stage.close();
				}
			} else {
				// no account available
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("We cannot find any account with such ID");
				alert.setContentText("Please make sure you are entering a correct ID");
				alert.showAndWait();
			}
			for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
			  System.out.println(document.getId());
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loginAdmin(long accountId, String password) {
		CollectionReference users = db.collection("admins");
		// Create a query against the collection.
		Query query = users.whereEqualTo("accountId", accountId);
		// retrieve  query results asynchronously using query.get()
		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		
		try {
			List<QueryDocumentSnapshot> data = querySnapshot.get().getDocuments();
			if (data.size() > 0) {
				DocumentSnapshot document = data.get(0);
				if (document.getString("password").equals(password)) {
				System.out.println(document.getString("name"));
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Invalid credentails!");
					alert.setContentText("Please make sure you have entered a correct password.");
					alert.showAndWait();
				}
				
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Not authorized!");
				alert.setContentText("You are not an administrator. Please login as a normal user");
				alert.showAndWait();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

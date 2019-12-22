import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminPanel {
	Stage stage;
	Label accName;
	Font navFont = new Font(16);
	Firestore db;

	Label txtUsers;
	Label txtApprove;
	Label txtTransactions;
	Label txtSuspends;
	
	XYChart.Series dataSeries1, dataSeries2, dataSeries3;

	public AdminPanel(DocumentSnapshot data) {
		Constant.ADMIN = true;
		stage = new Stage();
		stage.setTitle("AIAS Banking Admin Panel");
		stage.setResizable(false);

		VBox vBox = new VBox();

		HBox hBox = new HBox();
		HBox header = new HBox(20);
		VBox nav = new VBox(10);

		header.setPrefHeight(200);
		header.setAlignment(Pos.CENTER);
		Label title = new Label("AIAS Banking Admin Panel");
		title.setFont(new Font(30));

		FileInputStream input;
		Image image;
		ImageView imageView = new ImageView();
		imageView.setFitWidth(25);
		imageView.setFitHeight(25);
		try {
			input = new FileInputStream("src/main/resources/user.png");
			image = new Image(input);
			imageView.setImage(image);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		accName = new Label(data.getString("name"));
		accName.setFont(new Font(24));

		Region region= new Region();
		header.setHgrow(region, Priority.ALWAYS);


		Region r2 = new Region();
		r2.setPrefWidth(10);

		Region r3 = new Region();
		r3.setPrefWidth(10);

		Separator separator = new Separator();

		Button btnLogout = new Button("Logout");

		btnLogout.setFont(new Font(15));
		btnLogout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
				new LoginPage();
			}
		});

		header.getChildren().addAll(r2,title,region,imageView,accName,btnLogout,r3);

		nav.setAlignment(Pos.TOP_CENTER);
		nav.setPrefWidth(200);
		nav.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY,Insets.EMPTY)));

		TextField search = new TextField();
		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);

		HBox searchBar = new HBox(10, search, btnSearch);
		searchBar.setPadding(new Insets(10, 10, 0, 10));

		Button btnUsers =new Button("Users");
		btnUsers.setPrefWidth(180);
		btnUsers.setFont(navFont);
		btnUsers.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new Users();
			}
		});

		Button btnAdmins =new Button("Admins");
		btnAdmins.setPrefWidth(180);
		btnAdmins.setFont(navFont);
		btnAdmins.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new Admins();
			}
		});

		Button btnRequests =new Button("Loan requests");
		btnRequests.setPrefWidth(180);
		btnRequests.setFont(navFont);
		btnRequests.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new LoansHistory(0);
			}
		});

		nav.getChildren().addAll(searchBar, btnUsers, btnAdmins, btnRequests);
		nav.setPrefHeight(400);

		txtUsers = new Label("0");
		txtUsers.setFont(new Font(18));

		Label l1 = new Label("registered users");
		VBox totalUsers = new VBox(5, txtUsers, l1);
		totalUsers.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10),Insets.EMPTY)));
		totalUsers.setPadding(new Insets(10));

		txtApprove = new Label("0");
		txtApprove.setFont(new Font(18));

		Label l2 = new Label("approval requests");
		VBox approvals = new VBox(txtApprove, l2);
		approvals.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10),Insets.EMPTY)));
		approvals.setPadding(new Insets(10));

		txtTransactions = new Label("0");
		txtTransactions.setFont(new Font(18));
		Label l3 = new Label("total transactions");

		VBox totalTransactions = new VBox(5, txtTransactions, l3);
		totalTransactions.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10),Insets.EMPTY)));
		totalTransactions.setPadding(new Insets(10));

		txtSuspends = new Label("0");
		txtSuspends.setFont(new Font(18));
		Label l4 = new Label("total suspended");
		txtSuspends.setTextFill(Color.WHITE);
		l4.setTextFill(Color.WHITE);
		VBox suspends = new VBox(5, txtSuspends, l4);
		suspends.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10),Insets.EMPTY)));
		suspends.setPadding(new Insets(10));

		HBox firstRow = new HBox(10, totalUsers, approvals);
		HBox secondRow = new HBox(10, totalTransactions, suspends);
		firstRow.setHgrow(totalUsers, Priority.ALWAYS);
		firstRow.setHgrow(approvals, Priority.ALWAYS);
		secondRow.setHgrow(totalTransactions, Priority.ALWAYS);
		secondRow.setHgrow(suspends, Priority.ALWAYS);

		CategoryAxis xAxis    = new CategoryAxis();
		xAxis.setLabel("Statistics");

		NumberAxis yAxis = new NumberAxis();

		BarChart barChart = new BarChart(xAxis, yAxis);

		dataSeries1 = new XYChart.Series();
		dataSeries1.setName("Registered");
		
		dataSeries2 = new XYChart.Series();
		dataSeries2.setName("Approved");
		
		dataSeries3 = new XYChart.Series();
		dataSeries3.setName("Suspended");

        barChart.getData().addAll(dataSeries1, dataSeries2, dataSeries3);

		VBox body = new VBox(10, firstRow, secondRow, barChart);
		body.setPadding(new Insets(10));

		Separator separator1 = new Separator(Orientation.VERTICAL);
		hBox.getChildren().addAll(nav,separator1, body);
		hBox.setHgrow(body, Priority.ALWAYS);
		vBox.getChildren().addAll(header, separator, hBox);

		Scene scene = new Scene(vBox, 800, 500);
		stage.setScene(scene);

		db = FirestoreClient.getFirestore();
		listenData();

		stage.show();
	}
	void listenData()
	{
		System.out.println("Listening");
		db.collection("info").document("stats").addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(final DocumentSnapshot value, FirestoreException error) {
				if (error != null) {
					System.err.println("Listen failed:" + error);
					return;
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						txtUsers.setText(value.getLong("users")+"");
						txtApprove.setText(value.getLong("requests")+"");
						txtTransactions.setText(value.getLong("transactions")+"");
						txtSuspends.setText(value.getLong("suspended")+"");


						dataSeries1.getData().add(new XYChart.Data("Registered", value.getLong("users")));
				        dataSeries2.getData().add(new XYChart.Data("Transactions", value.getLong("requests")));
				        dataSeries3.getData().add(new XYChart.Data("Suspended", value.getLong("suspended")));
						
					}
				});

			}
		});
	}

}

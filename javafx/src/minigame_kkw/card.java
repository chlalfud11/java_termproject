package minigame_kkw;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class card extends Application {
    private int gridSize = 2; // 시작 크기 2x2
    private Button[][] buttons;
    private int[][] answer;
    private Button firstClick = null;
    private int firstRow = -1, firstCol = -1;
    private int matchCount = 0;

    @Override
    public void start(Stage stage) {
        initializeGame(stage);
    }

    private void initializeGame(Stage stage) {
        // GridPane에 버튼 추가
        GridPane grid = new GridPane();
        grid.setHgap(10); // 카드 간의 가로 간격
        grid.setVgap(10); // 카드 간의 세로 간격
        buttons = new Button[gridSize][gridSize];
        answer = new int[gridSize][gridSize];
        matchCount = 0;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(100, 100); // 카드 크기 설정
                button.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-radius: 10;");
                button.setGraphic(new ImageView(new Image("file:src/img/back_img.PNG"))); // 뒷면 이미지
                buttons[i][j] = button;

                int finalI = i;
                int finalJ = j;

                button.setOnAction(e -> handleCardClick(button, finalI, finalJ, stage));
                grid.add(button, j, i);
            }
        }

        initAnswer();

        // 카드 그리드를 StackPane으로 감싸서 화면 가운데 배치
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER); // 가운데 정렬
        root.getChildren().add(grid);

        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.setTitle("카드 매칭 게임");
        stage.show();
    }

    private void initAnswer() {
        List<Integer> cardValues = new ArrayList<>();
        for (int i = 0; i < (gridSize * gridSize) / 2; i++) {
            cardValues.add(i);
            cardValues.add(i); // 같은 값 두 개 추가
        }
        Collections.shuffle(cardValues);

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                answer[i][j] = cardValues.get(index++);
            }
        }
    }

    private void handleCardClick(Button button, int row, int col, Stage stage) {
        if (button.getStyle().contains("gray")) return; // 이미 매칭된 카드 클릭 방지

        button.setGraphic(new ImageView(new Image("file:src/img/cardimg" + answer[row][col] + ".PNG"))); // 앞면 이미지

        if (firstClick == null) {
            // 첫 번째 클릭
            firstClick = button;
            firstRow = row;
            firstCol = col;
        } else {
            // 두 번째 클릭
            if (button == firstClick) {
                return; // 동일한 카드 클릭 방지
            }

            if (answer[row][col] == answer[firstRow][firstCol]) {
                // 카드 매칭 성공
                button.setStyle("-fx-background-color: gray; -fx-border-color: black;");
                firstClick.setStyle("-fx-background-color: gray; -fx-border-color: black;");
                button.setDisable(true);
                firstClick.setDisable(true);
                matchCount++;

                if (matchCount == (gridSize * gridSize) / 2) {
                    nextLevel(stage); // 모든 카드가 맞춰졌다면 다음 단계로
                }
            } else {
                // 카드 매칭 실패 - 0.5초 후 뒤집기
                Button tempFirst = firstClick;
                Button tempSecond = button;

                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> {
                    tempFirst.setGraphic(new ImageView(new Image("file:src/img/back_img.PNG"))); // 뒷면 이미지로 전환
                    tempSecond.setGraphic(new ImageView(new Image("file:src/img/back_img.PNG"))); // 뒷면 이미지로 전환
                });
                pause.play();
            }

            firstClick = null; // 첫 번째 클릭 초기화
        }
    }

    private void nextLevel(Stage stage) {
        if (gridSize < 8) {
            gridSize += 2; // 크기 증가
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("레벨 업!");
            alert.setHeaderText(null);
            alert.setContentText("다음 레벨로 이동합니다! " + gridSize + "x" + gridSize);
            alert.showAndWait();
            initializeGame(stage); // 게임 초기화
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("게임 완료!");
            alert.setHeaderText(null);
            alert.setContentText("모든 레벨을 완료했습니다! 축하합니다!");
            alert.showAndWait();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Copyright 2017 K.Koike
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.seapanda.bunnyhop.view.node;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.seapanda.bunnyhop.common.Vec2D;
import net.seapanda.bunnyhop.common.constant.BhParams;
import net.seapanda.bunnyhop.common.tools.MsgPrinter;
import net.seapanda.bunnyhop.configfilereader.FXMLCollector;
import net.seapanda.bunnyhop.model.node.TextNode;
import net.seapanda.bunnyhop.view.node.part.BhNodeViewStyle;
import net.seapanda.bunnyhop.view.node.part.ImitationCreator;
import net.seapanda.bunnyhop.viewprocessor.NodeViewProcessor;

/**
 * ラベルを入力フォームに持つビュー
 * @author K.Koike
 */
public class LabelNodeView extends BhNodeView implements ImitationCreator {

	private Label label = new Label();
	private final TextNode model;
	private Button imitCreateImitBtn;	//!< イミテーション作成ボタン

	public LabelNodeView(TextNode model, BhNodeViewStyle viewStyle) {
		super(viewStyle, model);
		this.model = model;
	}

	/**
	 * 初期化する
	 */
	public boolean init() {

		initialize();
		boolean success = loadComponent();
		getTreeManager().addChild(label);

		if (model.canCreateImitManually) {
			Optional<Button> btnOpt = loadButton(BhParams.Path.IMIT_BUTTON_FXML, viewStyle.imitation);
			success &= btnOpt.isPresent();
			imitCreateImitBtn = btnOpt.orElse(new Button());
			getTreeManager().addChild(imitCreateImitBtn);
		}

		initStyle(viewStyle);
		setFuncs(this::updateShape, null);
		return success;
	}

	private void initStyle(BhNodeViewStyle viewStyle) {

		label.autosize();
		label.setMouseTransparent(true);
		label.setTranslateX(viewStyle.paddingLeft);
		label.setTranslateY(viewStyle.paddingTop);
		label.getStyleClass().add(viewStyle.label.cssClass);
		label.heightProperty().addListener(newValue -> getAppearanceManager().updateAppearance(null));
		label.widthProperty().addListener(newValue -> getAppearanceManager().updateAppearance(null));
		getAppearanceManager().addCssClass(BhParams.CSS.CLASS_LABEL_NODE);
	}

	/**
	 * GUI部品をロードする
	 * @return ロードに成功した場合 true. 失敗した場合 false.
	 * */
	private boolean loadComponent() {

		String inputControlFileName = BhNodeViewStyle.nodeID_inputControlFileName.get(model.getID());
		if (inputControlFileName != null) {
			Path filePath = FXMLCollector.INSTANCE.getFilePath(inputControlFileName);
			try {
				FXMLLoader loader = new FXMLLoader(filePath.toUri().toURL());
				label = (Label)loader.load();
			} catch (IOException | ClassCastException e) {
				MsgPrinter.INSTANCE.errMsgForDebug(
					"failed to initialize " + LabelNodeView.class.getSimpleName() + "\n" + e.toString());
				return false;
			}
		}
		return false;
	}


	/**
	 * このビューのモデルであるBhNodeを取得する
	 * @return このビューのモデルであるBhNode
	 */
	@Override
	public TextNode getModel() {
		return model;
	}

	/**
	 * モデルの構造を表示する
	 * @param depth 表示インデント数
	 * */
	@Override
	public void show(int depth) {
		MsgPrinter.INSTANCE.msgForDebug(indent(depth) + "<LabelView" + ">   " + this.hashCode());
		MsgPrinter.INSTANCE.msgForDebug(indent(depth + 1) + "<content" + ">   " + label.getText());
	}

	/**
	 * ノードの大きさや見た目を変える
	 * */
	private void updateShape(BhNodeViewGroup child) {

		viewStyle.width = label.getWidth();
		viewStyle.height = label.getHeight();
		getAppearanceManager().updatePolygonShape();
		if (parent.get() != null) {
			parent.get().rearrangeChild();
		}
		else {
			Vec2D pos = getPositionManager().getPosOnWorkspace();	//workspace からの相対位置を計算
			getPositionManager().setPosOnWorkspace(pos.x, pos.y);
		}
	}

	public String getText() {
		return label.getText();
	}

	public void setText(String text) {
		label.setText(text);
	}

	@Override
	public Button imitCreateButton() {
		return imitCreateImitBtn;
	}

	@Override
	public void accept(NodeViewProcessor visitor) {
		visitor.visit(this);
	}
}















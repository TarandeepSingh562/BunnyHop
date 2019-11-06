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
package net.seapanda.bunnyhop.modelprocessor;

import java.util.Deque;
import java.util.LinkedList;

import net.seapanda.bunnyhop.model.imitation.Imitatable;
import net.seapanda.bunnyhop.model.imitation.ImitationID;
import net.seapanda.bunnyhop.model.node.BhNode;
import net.seapanda.bunnyhop.model.node.TextNode;
import net.seapanda.bunnyhop.model.node.connective.ConnectiveNode;
import net.seapanda.bunnyhop.undo.UserOperationCommand;

/**
 * イミテーションノードツリーを作成するクラス
 * @author K.Koike
 */
public class ImitationBuilder implements BhModelProcessor {

	private final Deque<Imitatable> parentImitStack = new LinkedList<>();	//!< 現在処理中のBhNode の親がトップにくるスタック
	UserOperationCommand userOpeCmd;	//!< undo用コマンドオブジェクト
	private boolean isManualCreation;	//!< トップノードのイミテーションを手動作成する場合true

	/**
	 * ノードを付け変えた結果, 自動的に作成されるイミテーションノードを作る
	 * @param node イミテーションを作成するオリジナルノード
	 * @param userOpeCmd undo用コマンドオブジェクト
	 * @return 作成したイミテーションノードツリーのトップノード
	 * */
	public static Imitatable buildForAutoCreation(BhNode node, UserOperationCommand userOpeCmd) {
		var builder = new ImitationBuilder(userOpeCmd, false);
		node.accept(builder);
		return builder.parentImitStack.peekLast();
	}

	/**
	 * イミテーション作成操作を手動で行った結果できるイミテーションノードを作る
	 * @param node イミテーションを作成するオリジナルノード
	 * @param userOpeCmd undo用コマンドオブジェクト
	 * @return 作成したイミテーションノードツリーのトップノード
	 * */
	public static Imitatable buildForManualCreation(BhNode node, UserOperationCommand userOpeCmd) {
		var builder = new ImitationBuilder(userOpeCmd, true);
		node.accept(builder);
		return builder.parentImitStack.peekLast();
	}

	private ImitationBuilder(UserOperationCommand userOpeCmd, boolean isManualCreation) {
		this.userOpeCmd = userOpeCmd;
		this.isManualCreation = isManualCreation;
	}

	/**
	 * @param node イミテーションを作成して、入れ替えを行いたいオリジナルノード
	 */
	@Override
	public void visit(ConnectiveNode node) {

		ImitationID imitID = null;
		if (isManualCreation) {
			imitID = ImitationID.MANUAL;
			isManualCreation = false;
		}
		else if (node.getParentConnector() != null) {
			imitID = node.getParentConnector().findImitationID();
		}

		if (!node.imitationNodeExists(imitID))
			return;

		if (parentImitStack.isEmpty()) {
			ConnectiveNode newImit = node.createImitNode(imitID, userOpeCmd);
			parentImitStack.addLast(newImit);
			node.sendToSections(this);
			NodeMVCBuilder.build(newImit);
			TextImitationPrompter.prompt(newImit);
		}
		else {
			Imitatable parentImit = parentImitStack.peekLast();
			//接続先を探す
			BhNode oldImit = ImitTaggedChildFinder.find(parentImit, node.getParentConnector().getImitCnctPoint());
			if (oldImit != null) {
				ConnectiveNode newImit = node.createImitNode(imitID, userOpeCmd);
				oldImit.replacedWith(newImit, userOpeCmd);
				parentImitStack.addLast(newImit);
				node.sendToSections(this);
				parentImitStack.removeLast();
			}
		}
	}

	@Override
	public void visit(TextNode node) {

		ImitationID imitID = null;
		if (isManualCreation) {
			imitID = ImitationID.MANUAL;
			isManualCreation = false;
		}
		else if (node.getParentConnector() != null) {
			imitID = node.getParentConnector().findImitationID();
		}

		if (!node.imitationNodeExists(imitID))
			return;

		if (parentImitStack.isEmpty()) {
			TextNode newImit = node.createImitNode(imitID, userOpeCmd);
			parentImitStack.addLast(newImit);
			NodeMVCBuilder.build(newImit);
			TextImitationPrompter.prompt(newImit);
		}
		else {
			Imitatable parentImit = parentImitStack.peekLast();
			//接続先を探す
			BhNode oldImit = ImitTaggedChildFinder.find(parentImit, node.getParentConnector().getImitCnctPoint());
			if (oldImit != null) {
				TextNode newImit = node.createImitNode(imitID, userOpeCmd);
				oldImit.replacedWith(newImit, userOpeCmd);
			}
		}
	}
}

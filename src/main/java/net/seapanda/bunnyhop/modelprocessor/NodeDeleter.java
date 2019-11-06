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

import net.seapanda.bunnyhop.message.MsgService;
import net.seapanda.bunnyhop.model.node.BhNode;
import net.seapanda.bunnyhop.model.node.TextNode;
import net.seapanda.bunnyhop.model.node.VoidNode;
import net.seapanda.bunnyhop.model.node.connective.ConnectiveNode;
import net.seapanda.bunnyhop.undo.UserOperationCommand;

/**
 * ノードツリーの削除を行うクラス
 * @author K.Koike
 */
public class NodeDeleter implements BhModelProcessor {

	UserOperationCommand userOpeCmd;	//!< undo用コマンドオブジェクト

	/**
	 * 引数で指定したノード以下のノードを削除する
	 * @param node このノード以下のノードを削除する.
	 * @param userOpeCmd undo用コマンドオブジェクト
	 * */
	public static void delete(BhNode node, UserOperationCommand userOpeCmd) {
		node.accept(new NodeDeleter(userOpeCmd));
	}

	/**
	 * コンストラクタ
	 * @param userOpeCmd undo用コマンドオブジェクト
	 */
	private NodeDeleter(UserOperationCommand userOpeCmd) {
		this.userOpeCmd = userOpeCmd;
	}

	/**
	 * node の削除処理を行う
	 * @param node 削除するノード
	 * */
	@Override
	public void visit(ConnectiveNode node) {

		MsgService.INSTANCE.removeNodeToPaste(node, userOpeCmd);

		//このノードがイミテーションノードだった場合, オリジナルにイミテーションが消えたことを伝える
		if (node.isImitationNode())
			node.getOriginal().disconnectOrgImitRelation(node, userOpeCmd);

		node.sendToSections(this);
		node.deleteAllImitations(userOpeCmd);	//オリジナルが消えた場合, イミテーションも消える
	}

	@Override
	public void visit(VoidNode node) {
		MsgService.INSTANCE.removeNodeToPaste(node, userOpeCmd);
	}

	@Override
	public void visit(TextNode node) {

		MsgService.INSTANCE.removeNodeToPaste(node, userOpeCmd);

		//このノードがイミテーションノードだった場合, オリジナルにイミテーションが消えたことを伝える
		if (node.isImitationNode())
			node.getOriginal().disconnectOrgImitRelation(node, userOpeCmd);

		node.deleteAllImitations(userOpeCmd);	//オリジナルが消えた場合, イミテーションも消える
	}
}

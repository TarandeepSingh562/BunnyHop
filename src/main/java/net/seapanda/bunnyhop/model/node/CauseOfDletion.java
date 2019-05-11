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
package net.seapanda.bunnyhop.model.node;

/**
 * ノードの削除原因
 * @author K.Koike
 * */
public enum CauseOfDletion {

	/**
	 * イミテーションがオリジナルノードの削除の影響を受けた.
	 * オリジナルノードが削除される際は, 必ず原因を INFLUENCE_OF_ORIGINAL_DELETION として削除イベント処理を呼ぶ.
	 * */
	INFLUENCE_OF_ORIGINAL_DELETION,
	TRASH_BOX,	//!< ゴミ箱に
	SYNTAX_ERROR,	//!< 構文エラーノードの削除
	SELECTED_FOR_DELETION,	//!< 選択削除の対象になった
	WORKSPACE_DELETION;	//!< ワークスペースの削除

	public boolean eq(CauseOfDletion cause) {
		return cause == this;
	}
}

let imitIdManual = 'imitIdManual';

function genReusableBarrierDeclTemplateNode(nodeID) {

	let barrier = bhCommon.buildImitation(bhThis, imitIdManual, bhUserOpeCmd);
	let templateNode = bhCommon.genBhNode(nodeID, bhNodeTemplates, bhUserOpeCmd);
	let barrierVarArg = templateNode.findSymbolInDescendants('*', 'Arg0', '*');
	barrierVarArg.replace(barrier, bhUserOpeCmd);
	return templateNode;
}

(function() {

	let imit = bhCommon.buildImitation(bhThis, imitIdManual, bhUserOpeCmd);
	return [
		imit,
		genReusableBarrierDeclTemplateNode('idAwaitStat'),
		genReusableBarrierDeclTemplateNode('idGetNumberWaitingExp')];
})();

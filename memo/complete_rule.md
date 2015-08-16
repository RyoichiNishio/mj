#計算機で麻雀を定義する

麻雀をプレイするアルゴリズムを作るために、計算機上で麻雀というゲームを定義する。

## 局面(Phase)の定義
局面とは、PlayerがActionを選択する場面であるとする。
局面を定めさえすれば、Playerがルール上取りうるすべてのActionが列挙できるようなものであるとする。
局面とPlayerの選択したActionが与えられれば、次の局面が定まらなければならない。(次の局面は確率的に定まるとしても良い。)

    例えば、将棋においては、盤面と持ち駒と先手後手どちらの手番か、という情報だけでは局面とはならない。千日手の判定のために数手前までの履歴が必要となるためである。

## 麻雀の局面
- 1Playerが打牌などを選択する局面と、3Playersが鳴きなどを選択する局面の２つに大別する。前者をActionPhase、後者をReactionPhaseと呼ぶことにする。
    - "これからツモる"場面は局面ではない。なぜならPlayerに選択の余地はなく、ツモらねばいけない場面であるから。
- 局面には、それぞれの手牌、副露状況、河、点数状況、残り山数、ドラなどが含まれることは明らかである。しかしこれだけか。
    - 同じ盤面でも、ツモ直後ならツモあがりできるが、ポン直後の場合できない。このように、contextを定めないと、可能手を決定できない。定めるべきcontextをPhaseContextと呼ぶことにする。
- 厳密には他の情報から計算できるものも、毎回計算するのが大変なものに関しては変数として保存しておくことにする。(フリテンなど)
    - それぞれのPlayerがテンパイ(形式テンパイ含む)かどうか。
    - (形式)テンパイの場合、待ちは何か。(形式テンパイを含める理由は、状況によって役が付くかどうかが変わるからである。ホウテイなど)
    - それぞれのPlayerが、フリテンかどうか (isFuriten)
        - Playerが更新されるたびに、isFuritenは再計算される。
        - 正確には、ActionPhaseが回ってくるたびに、isFuritenはfalseになる。
        - ただし、テンパイかつ待ちが河にある場合trueになる。
        - さらに、ActionPhaseが回ってこなくても、見逃しをするとtrueになる。
    - それぞれのPlayerが、面前か、リーチ宣言か、リーチか、ダブルリーチか、仕掛けか。(playerStatus)
    - 仕掛けのない一巡目か(isFirstTurnWithoutMeld)
    - 残り山数(wall.rest)
    - カンされた数(nKang)

# ActionPhase

- ActionPhaseContext
    - ツモ直後、アンカン直後、ミンカン直後(カカンまたはダイミンカン)、ポンチー直後

## ActionPhaseにおける可能手と可能手となるための条件

- ツモあがり
    - あがり形である
    - PhaseContext: ツモ直後、アンカン直後、ミンカン直後
    - playerStatus: すべて (players(turnPlayer).playerStatusで参照する)
    - 1ハン以上あるか。また、あがった場合にはScoringを行うので、Scoringに必要な情報すべてがこのActionPhaseに存在していないといけない。
        - 手役がつくかどうかを手牌とフーロ状況から判定
        - playerStatusから、メンゼンツモがつくかどうかを判定
        - リーチやダブルリーチによってプラス1(2)ハンつくかどうかを判定
        - 嶺上開花がつくかどうかをPhaseContextから判定
        - 天和あるいは地和がつくかどうかをisFirstTurnWithoutMeldによって判定
        - ハイテイがつくかどうかをwall.restによって判定
        - ドラの計算。表ドラはfield.doraIndicatingTilesによって計算。リーチの場合は、field.doraIndicatingTiles.lengthの数だけ裏ドラをめくる。
        - depositとroundの情報を参照して点数を補正する。
- リーチ宣言
    - シャンテン数が0以下である。(あがり形でもよい)
    - PhaseContext: ツモ直後かアンカン直後
    - playerStatus: 面前
    - 点数1000点以上
    - wall.rest>=4
- アンカン
    - handに同一のTileKindが4枚含まれる
    - アンカン候補は複数あり得るので、アンカンの際にはTileKindを指定する
    - PhaseContext: ツモ直後、アンカン直後、ミンカン直後
    - playerStatus: すべて
    - 5回目のカンでない。nKang<=3なら良い。
    - wall.rest>=1
    - PlayerStatus: リーチ、ダブルリーチの場合、送りカンなどが禁止であることに注意
- カカン
    - ポンしたTileKindと同一のTileKindが手牌にある
    - PhaseContext: ツモ直後、アンカン直後、ミンカン直後
    - playerStatus: 仕掛け
    - 5回目のカンでない。nKang<=3なら良い。
    - wall.rest>=1
- 打牌
    - その牌がhandに含まれている。ただし状況によっては打牌できる牌は以下のように限られる。
    - PhaseContext: すべて
        - PhaseContextによる条件はない
    - playerStatus: すべて
        - リーチ、ダブルリーチの場合、ツモ切りのみ。
        - リーチ宣言の場合、シャンテン数が0になる牌のみ。
        - ポンチー直後の場合、食い変えになる牌は打牌できない。
            - この場合、player.openSet.last :OpenSetから打牌できない牌を列挙する。handのすべてが食い変えになるような鳴きは初めからできないようにする。
- 九種九牌
    - 手牌が九種九牌の条件を満たしている。
    - isFirstTurnWithoutMeldedがtrueである
    - PhaseContext: ツモ直後
    - playerStatus: 面前

# ReactionPhase

- ReactionPhaseContext
    - 河への打牌、カカン直後

## ReactionPhaseにおける可能手と可能手となるための条件

- ロンあがり
    - あがり形である
    - PhaseContext: すべて
    - PlayerStatus: すべて
    - 1ハン以上あるか。また、あがった場合にはScoringを行うので、Scoringに必要な情報すべてがこのActionPhaseに存在していないといけない。
        - 手役がつくかどうかを手牌とフーロ状況から判定
        - リーチやダブルリーチによってプラス1(2)ハンつくかどうかを判定
        - チャンカンがつくかどうかをPhaseContextから判定
        - ホウテイがつくかどうかをwall.restによって判定
        - ドラの計算。表ドラはfield.doraIndicatingTilesによって計算。リーチの場合は、field.doraIndicatingTiles.lengthの数だけ裏ドラをめくる。
        - depositとroundの情報を参照して点数を補正する。
    - isFuriten=falseでなければならない。
- ダイミンカン
    - 打牌のTileKindを3枚handに持っている
    - PhaseContext:　河への打牌
    - PlayerStatus: 面前、仕掛け
    - 5回目のカンでない。nKang<=3なら良い。
    - wall.rest>=1
- ポン
    - 打牌のTileKindを2枚以上handに持っている
    - PhaseContext: 河への打牌
    - PlayerStatus: 面前、仕掛け
    - wall.rest>=1
    - 喰い変えしかできないという状況にならない
    - 3枚持っている場合、どの1枚を残すかという選択が存在する(特に赤5が混ざる際重要)
- チー
    - 打牌のTileKindとhandの2枚を使ってShuntsuを作れる
    - turnPlayerが上家である
    - PhaseContext: 河への打牌
    - PlayerStatus: 面前、仕掛け
    - wall.rest>=1
    - 喰い変えしかできないという状況にならない
    - 複数持っている場合、どの牌を残すかという選択が存在する(特に赤5が混ざる際重要)
- スキップ
    - 常に可能

# Actionを選択後の処理
##ActionPhaseにおける可能手と処理

- ツモあがり
    - Scoringへ
- リーチ宣言
    - 次もActionPhase
    - PhaseContextの変更はしない
    - playerStatusをリーチ宣言にする
    - pondにReachEventを追加
- アンカン
    - PhaseContext: ミンカン直後の場合、カンドラをめくる。めくるカンドラはnKangの値で判定。
    - 次もActionPhase
    - nKangを+1する。
    - カンドラをめくる。
    - アンカンに選択した4牌をhandから削除し、openSetに加える。
    - リンシャンツモする。
    - PhaseContext: アンカン直後にする。
    - pondにMeldEventを追加。
- カカン
    - PhaseContext: ミンカン直後の場合、カンドラをめくる。めくるカンドラはnKangの値で判定。
    - nKangを+1する。
    - ReactionPhaseへ
    - PhaseContext: カカン直後
    - pondにMeldEventを追加。
- 打牌
    - PhaseContext: ミンカン直後の場合、カンドラをめくる。めくるカンドラはnKangの値で判定。
    - 打牌した牌をhandから取り除く。
    - context:河のReactionPhaseへ。
    - isFuritenはfalseにする。ただし河にアタリ牌がある場合trueにする。またリーチ後の場合はtrueからfalseに更新したりはしない。

## ReactionPhase

- 一人以上がロンあがり
    - Scoreingへ
    - 3人ロンならトリロンで流局
    - 2人ロンは清算。一人ロンも清算。
- 誰かがダイミンカン
    - players(turnPlayer).statusがリーチ宣言の場合、リーチあるいはダブルリーチにする。depositに1000点。
    - nKangを+1する。
    - リンシャンツモする。
    - PhaseContext: ミンカン直後にする。
    - ActionPhase
    - pondにMeldEventを追加
    - あがれるのにスキップしたPlayerをフリテンにする
- 誰かがポン
    - players(turnPlayer).statusがリーチ宣言の場合、リーチあるいはダブルリーチにする。depositに1000点。
    - PhaseContext: ポンチー直後にする。
    - ActionPhase
    - pondにMeldEventを追加
    - あがれるのにスキップしたPlayerをフリテンにする
- 誰かがチー
    - players(turnPlayer).statusがリーチ宣言の場合、リーチあるいはダブルリーチにする。depositに1000点。
    - PhaseContext: ポンチー直後にする。
    - ActionPhase
    - pondにMeldEventを追加
    - あがれるのにスキップしたPlayerをフリテンにする
- スキップ
    - players(turnPlayer).statusがリーチ宣言の場合、リーチあるいはダブルリーチにする。
    - 流局判定
        - nKang>=4 かつカンしたPlayerが二人以上で流局
        - isFirstTurnWithoutMeldがtrueかつwall.rest=66の場合に、4つの捨て牌がすべて同じ風の場合に流局
        - players(turnPlayer).statusがリーチ宣言の場合、リーチあるいはダブルリーチにする。この際4人目のリーチだった場合流局
        - wall.length == 0
    - turnPlayerを+1して%4にする
    - ツモする
    - ActionPhase
    - あがれるのにスキップしたPlayerをフリテンにする

#点数計算(Scoring)

子の場合
(符)*4*4*2^(ハン)
ツモならこれを1:1:2に分割する。10の位は切り上げる。親は1.5倍。


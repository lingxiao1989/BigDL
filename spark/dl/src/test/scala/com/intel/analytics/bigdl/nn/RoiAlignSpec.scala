/*
 * Copyright 2016 The BigDL Authors.
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

package com.intel.analytics.bigdl.nn

import com.intel.analytics.bigdl.tensor.{Storage, Tensor}
import com.intel.analytics.bigdl.utils.serializer.ModuleSerializationTest
import com.intel.analytics.bigdl.utils.{T, Table}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class RoiAlignSpec extends FlatSpec with Matchers {
  "updateOutput Float type" should "work properly" in {
    val spatio_scale: Float = 1.0f
    val sampling_ratio: Int = 3
    val pooled_height: Int = 2
    val pooled_width: Int = 2

    val data = Array(
      0.327660024166107178, 0.783334434032440186, 0.359168112277984619,
      0.934897661209106445, 0.650066614151000977, 0.834474444389343262,
      0.424300372600555420, 0.149160504341125488, 0.730795919895172119,
      0.484096407890319824, 0.994338274002075195, 0.250495135784149170,
      0.259522974491119385, 0.887678027153015137, 0.194342792034149170,
      0.610941588878631592, 0.416747927665710449, 0.705707132816314697,
      0.435783147811889648, 0.778170645236968994, 0.193895518779754639,
      0.849628329277038574, 0.882959723472595215, 0.721439063549041748,
      0.832545340061187744, 0.774163544178009033, 0.781816542148590088,
      0.729343354701995850, 0.203778445720672607, 0.198633491992950439,
      0.781321287155151367, 0.118729889392852783, 0.643143951892852783,
      0.760397315025329590, 0.285254061222076416, 0.553620159626007080,
      0.232052326202392578, 0.728380799293518066, 0.775489747524261475,
      0.928656220436096191, 0.163158237934112549, 0.718611896038055420,
      0.744661569595336914, 0.593953251838684082, 0.372228324413299561,
      0.902524888515472412, 0.278600215911865234, 0.506435513496398926,
      0.818576753139495850, 0.757465600967407227, 0.705808222293853760,
      0.710981726646423340, 0.963726997375488281, 0.164355456829071045,
      0.780107796192169189, 0.850457072257995605, 0.839718520641326904,
      0.593321025371551514, 0.280547201633453369, 0.348339796066284180,
      0.423507034778594971, 0.949673593044281006, 0.518748283386230469,
      0.845408916473388672, 0.901987016201019287, 0.058945477008819580,
      0.631618440151214600, 0.488164126873016357, 0.698010146617889404,
      0.215178430080413818, 0.665156781673431396, 0.499578237533569336,
      0.863550186157226562, 0.088476061820983887, 0.177395820617675781,
      0.397035181522369385, 0.484034657478332520, 0.105176448822021484,
      0.095181167125701904, 0.111114203929901123, 0.715093195438385010,
      0.993503451347351074, 0.484178066253662109, 0.422980725765228271,
      0.192607104778289795, 0.983097016811370850, 0.638218641281127930,
      0.158814728260040283, 0.990248799324035645, 0.539387941360473633,
      0.657688558101654053, 0.316274046897888184, 0.851949751377105713,
      0.227342128753662109, 0.238007068634033203, 0.980791330337524414)

    val rois = Array(
      0, 0, 7, 5,
      6, 2, 7, 5,
      3, 1, 6, 4,
      3, 3, 3, 3)

    val input = new Table
    input.insert(Tensor(Storage(data.map(x => x.toFloat))).resize(1, 2, 6, 8))
    input.insert(Tensor(Storage(rois.map(x => x.toFloat))).resize(4, 4))

    val roiAlign = RoiAlign[Float](spatio_scale, sampling_ratio, pooled_height, pooled_width, "avg")
    val res = roiAlign.forward(input)
    val expectedRes = Array(
      0.614743709564208984, 0.550280153751373291,
      0.648947238922119141, 0.494060248136520386,
      0.514606714248657227, 0.596958041191101074,
      0.494195610284805298, 0.408652573823928833,
      0.707817792892456055, 0.494023799896240234,
      0.637864947319030762, 0.692903101444244385,
      0.308963924646377563, 0.266039490699768066,
      0.451879233121871948, 0.436514317989349365,
      0.393088847398757935, 0.704402685165405273,
      0.384622871875762939, 0.530835568904876709,
      0.525619626045227051, 0.501667082309722900,
      0.407763212919235229, 0.379031181335449219,
      0.566771149635314941, 0.329488337039947510,
      0.504409193992614746, 0.318125635385513306,
      0.405435621738433838, 0.409263730049133301,
      0.378736764192581177, 0.303221583366394043
    )

    for (i <- expectedRes.indices) {
      assert(Math.abs(res.storage().array()(i) - expectedRes(i)) < 1e-6)
    }
  }


  "updateOutput Onnx case" should "work properly" in {
    val spatio_scale: Float = 1.0f
    val sampling_ratio: Int = 2
    val pooled_height: Int = 5
    val pooled_width: Int = 5

    val data = Array(
      0.2764, 0.7150, 0.1958, 0.3416, 0.4638, 0.0259, 0.2963, 0.6518, 0.4856, 0.7250,
      0.9637, 0.0895, 0.2919, 0.6753, 0.0234, 0.6132, 0.8085, 0.5324, 0.8992, 0.4467,
      0.3265, 0.8479, 0.9698, 0.2471, 0.9336, 0.1878, 0.4766, 0.4308, 0.3400, 0.2162,
      0.0206, 0.1720, 0.2155, 0.4394, 0.0653, 0.3406, 0.7724, 0.3921, 0.2541, 0.5799,
      0.4062, 0.2194, 0.4473, 0.4687, 0.7109, 0.9327, 0.9815, 0.6320, 0.1728, 0.6119,
      0.3097, 0.1283, 0.4984, 0.5068, 0.4279, 0.0173, 0.4388, 0.0430, 0.4671, 0.7119,
      0.1011, 0.8477, 0.4726, 0.1777, 0.9923, 0.4042, 0.1869, 0.7795, 0.9946, 0.9689,
      0.1366, 0.3671, 0.7011, 0.6234, 0.9867, 0.5585, 0.6985, 0.5609, 0.8788, 0.9928,
      0.5697, 0.8511, 0.6711, 0.9406, 0.8751, 0.7496, 0.1650, 0.1049, 0.1559, 0.2514,
      0.7012, 0.4056, 0.7879, 0.3461, 0.0415, 0.2998, 0.5094, 0.3727, 0.5482, 0.0502
    )

    val rois = Array(
      0, 0, 9, 9,
      0, 5, 4, 9,
      5, 5, 9, 9)

    val input = new Table
    input.insert(Tensor(Storage(data.map(x => x.toFloat))).resize(1, 1, 10, 10))
    input.insert(Tensor(Storage(rois.map(x => x.toFloat))).resize(3, 4))

    val roiAlign = RoiAlign[Float](spatio_scale, sampling_ratio, pooled_height, pooled_width, "avg")
    val res = roiAlign.forward(input)
    val expectedRes = Array(
      0.4664, 0.4466, 0.3405, 0.5688, 0.6068,
      0.3714, 0.4296, 0.3835, 0.5562, 0.3510,
      0.2768, 0.4883, 0.5222, 0.5528, 0.4171,
      0.4713, 0.4844, 0.6904, 0.4920, 0.8774,
      0.6239, 0.7125, 0.6289, 0.3355, 0.3495,

      0.3022, 0.4305, 0.4696, 0.3978, 0.5423,
      0.3656, 0.7050, 0.5165, 0.3172, 0.7015,
      0.2912, 0.5059, 0.6476, 0.6235, 0.8299,
      0.5916, 0.7389, 0.7048, 0.8372, 0.8893,
      0.6227, 0.6153, 0.7097, 0.6154, 0.4585,

      0.2384, 0.3379, 0.3717, 0.6100, 0.7601,
      0.3767, 0.3785, 0.7147, 0.9243, 0.9727,
      0.5749, 0.5826, 0.5709, 0.7619, 0.8770,
      0.5355, 0.2566, 0.2141, 0.2796, 0.3600,
      0.4365, 0.3504, 0.2887, 0.3661, 0.2349
    )
    for (i <- expectedRes.indices) {
      assert(Math.abs(res.storage().array()(i) - expectedRes(i)) < 1e-4)
    }
  }

  "updateOutput Double type" should "work properly" in {
    val spatio_scale: Float = 1.0f
    val sampling_ratio: Int = 3
    val pooled_height: Int = 2
    val pooled_width: Int = 2

    val data = Array(
      0.327660024166107178, 0.783334434032440186, 0.359168112277984619,
      0.934897661209106445, 0.650066614151000977, 0.834474444389343262,
      0.424300372600555420, 0.149160504341125488, 0.730795919895172119,
      0.484096407890319824, 0.994338274002075195, 0.250495135784149170,
      0.259522974491119385, 0.887678027153015137, 0.194342792034149170,
      0.610941588878631592, 0.416747927665710449, 0.705707132816314697,
      0.435783147811889648, 0.778170645236968994, 0.193895518779754639,
      0.849628329277038574, 0.882959723472595215, 0.721439063549041748,
      0.832545340061187744, 0.774163544178009033, 0.781816542148590088,
      0.729343354701995850, 0.203778445720672607, 0.198633491992950439,
      0.781321287155151367, 0.118729889392852783, 0.643143951892852783,
      0.760397315025329590, 0.285254061222076416, 0.553620159626007080,
      0.232052326202392578, 0.728380799293518066, 0.775489747524261475,
      0.928656220436096191, 0.163158237934112549, 0.718611896038055420,
      0.744661569595336914, 0.593953251838684082, 0.372228324413299561,
      0.902524888515472412, 0.278600215911865234, 0.506435513496398926,
      0.818576753139495850, 0.757465600967407227, 0.705808222293853760,
      0.710981726646423340, 0.963726997375488281, 0.164355456829071045,
      0.780107796192169189, 0.850457072257995605, 0.839718520641326904,
      0.593321025371551514, 0.280547201633453369, 0.348339796066284180,
      0.423507034778594971, 0.949673593044281006, 0.518748283386230469,
      0.845408916473388672, 0.901987016201019287, 0.058945477008819580,
      0.631618440151214600, 0.488164126873016357, 0.698010146617889404,
      0.215178430080413818, 0.665156781673431396, 0.499578237533569336,
      0.863550186157226562, 0.088476061820983887, 0.177395820617675781,
      0.397035181522369385, 0.484034657478332520, 0.105176448822021484,
      0.095181167125701904, 0.111114203929901123, 0.715093195438385010,
      0.993503451347351074, 0.484178066253662109, 0.422980725765228271,
      0.192607104778289795, 0.983097016811370850, 0.638218641281127930,
      0.158814728260040283, 0.990248799324035645, 0.539387941360473633,
      0.657688558101654053, 0.316274046897888184, 0.851949751377105713,
      0.227342128753662109, 0.238007068634033203, 0.980791330337524414)

    val rois = Array(
      0, 0, 7, 5,
      6, 2, 7, 5,
      3, 1, 6, 4,
      3, 3, 3, 3)

    val input = new Table
    input.insert(Tensor(Storage(data.map(x => x))).resize(1, 2, 6, 8))
    input.insert(Tensor(Storage(rois.map(x => x.toDouble))).resize(4, 4))

    val roiAlign = RoiAlign[Double](
      spatio_scale, sampling_ratio, pooled_height, pooled_width, "avg")
    val res = roiAlign.forward(input)
    val expectedRes = Array(
      0.614743709564208984, 0.550280153751373291,
      0.648947238922119141, 0.494060248136520386,
      0.514606714248657227, 0.596958041191101074,
      0.494195610284805298, 0.408652573823928833,
      0.707817792892456055, 0.494023799896240234,
      0.637864947319030762, 0.692903101444244385,
      0.308963924646377563, 0.266039490699768066,
      0.451879233121871948, 0.436514317989349365,
      0.393088847398757935, 0.704402685165405273,
      0.384622871875762939, 0.530835568904876709,
      0.525619626045227051, 0.501667082309722900,
      0.407763212919235229, 0.379031181335449219,
      0.566771149635314941, 0.329488337039947510,
      0.504409193992614746, 0.318125635385513306,
      0.405435621738433838, 0.409263730049133301,
      0.378736764192581177, 0.303221583366394043
    )

    for (i <- expectedRes.indices) {
      assert(Math.abs(res.storage().array()(i) - expectedRes(i)) < 1e-6)
    }
  }
}

class RoiAlignSerialTest extends ModuleSerializationTest {
  override def test(): Unit = {
    val input = T()
    val input1 = Tensor[Float](1, 1, 2, 2).apply1(_ => Random.nextFloat())
    val input2 = Tensor[Float](1, 4).apply1(_ => Random.nextFloat())
    input(1.0f) = input1
    input(2.0f) = input2
    val roiAlign = new RoiAlign[Float](spatialScale = 1.0f, samplingRatio = 1,
      pooledW = 1, pooledH = 1).setName("roiAlign")
    runSerializationTest(roiAlign, input)
  }
}

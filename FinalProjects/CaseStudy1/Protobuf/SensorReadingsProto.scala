// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package sensor.sensor_readings

object SensorReadingsProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq.empty
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] =
    Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]](
      sensor.sensor_readings.SensorReading
    )
  private lazy val ProtoBytes: _root_.scala.Array[Byte] =
      scalapb.Encoding.fromBase64(scala.collection.immutable.Seq(
  """ChVzZW5zb3JfcmVhZGluZ3MucHJvdG8SBnNlbnNvciLHAQoNU2Vuc29yUmVhZGluZxIpCghzZW5zb3JJZBgBIAEoCUIN4j8KE
  ghzZW5zb3JJZFIIc2Vuc29ySWQSLAoJdGltZXN0YW1wGAIgASgDQg7iPwsSCXRpbWVzdGFtcFIJdGltZXN0YW1wEjIKC3RlbXBlc
  mF0dXJlGAMgASgCQhDiPw0SC3RlbXBlcmF0dXJlUgt0ZW1wZXJhdHVyZRIpCghodW1pZGl0eRgEIAEoAkIN4j8KEghodW1pZGl0e
  VIIaHVtaWRpdHliBnByb3RvMw=="""
      ).mkString)
  lazy val scalaDescriptor: _root_.scalapb.descriptors.FileDescriptor = {
    val scalaProto = com.google.protobuf.descriptor.FileDescriptorProto.parseFrom(ProtoBytes)
    _root_.scalapb.descriptors.FileDescriptor.buildFrom(scalaProto, dependencies.map(_.scalaDescriptor))
  }
  lazy val javaDescriptor: com.google.protobuf.Descriptors.FileDescriptor = {
    val javaProto = com.google.protobuf.DescriptorProtos.FileDescriptorProto.parseFrom(ProtoBytes)
    com.google.protobuf.Descriptors.FileDescriptor.buildFrom(javaProto, _root_.scala.Array(
    ))
  }
  @deprecated("Use javaDescriptor instead. In a future version this will refer to scalaDescriptor.", "ScalaPB 0.5.47")
  def descriptor: com.google.protobuf.Descriptors.FileDescriptor = javaDescriptor
}
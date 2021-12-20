package scalapb_openapi

import protocbridge.Artifact
import scalapb.GeneratorOption
import protocbridge.SandboxedJvmGenerator

object gen {
  def apply(
      options: GeneratorOption*
  ): (SandboxedJvmGenerator, Seq[String]) =
    (
      SandboxedJvmGenerator.forModule(
        "scala",
        Artifact(
          scalapb_openapi.compiler.BuildInfo.organization,
          "scalapb-openapi-plugin-codegen_2.12",
          scalapb_openapi.compiler.BuildInfo.version
        ),
        "scalapb_openapi.compiler.CodeGenerator$",
        scalapb_openapi.compiler.CodeGenerator.suggestedDependencies
      ),
      options.map(_.toString)
    )

  def apply(
      options: Set[GeneratorOption] = Set.empty
  ): (SandboxedJvmGenerator, Seq[String]) = apply(options.toSeq: _*)
}
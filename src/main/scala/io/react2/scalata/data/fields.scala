package io.react2.scalata.data

import io.react2.scalata.exceptions._
import io.react2.scalata.generators.Generator
import org.tsers.zeison.Zeison.JValue

/**
 * @author dbalduini
 */
object Field {
  def apply(fields: List[JValue]) = parseFields(fields)

  def parseFields(fields: List[JValue]): List[Field] = {
    def parse(j: JValue): Field = {
      val name = j.get[String]("name")
      val `type` = j.get[String]("type")
      val gen = Generator(`type`, j.generator)
      val fields = j.fields.toOption.map(_.toList).getOrElse(Nil)
      `type` match {
        case "{{object}}" => ObjField(name, parseFields(fields))
        case "{{string}}" => StringField(name)
        case "{{date}}" => DateField(name)
        case "{{number}}" => NumberField(name)
        case "{{boolean}}" => BooleanField(name)
        case "{{id}}" => IdField(name)
        case "{{null}}" => NullField
        case other => throw new InvalidFieldType(other)
      }
    }
    fields map parse
  }

}

case class Root(repeat: Int, fields: List[Field])

sealed abstract class Field {
  def name: String
}

case class ObjField(name: String, elems: List[Field]) extends Field

case class StringField(name: String) extends Field

case class DateField(name: String) extends Field

case class NumberField(name: String) extends Field

case class BooleanField(name: String) extends Field

case class IdField[T](name: String) extends Field

case object NullField extends Field {
  override val name: String = throw new NoSuchElementException("name of null field")
}

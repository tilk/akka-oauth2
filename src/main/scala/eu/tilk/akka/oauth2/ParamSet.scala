package eu.tilk.akka.oauth2

import scala.reflect.ClassTag
import scala.collection.{AbstractIterable, TraversableOnce}
import akka.http.scaladsl.model._

final class ParamSet[+T <: Param] private[oauth2] (params : Map[Class[_], T]) extends AbstractIterable[T] {
  def apply[T <: Param : ClassTag] = params(implicitly[ClassTag[T]].runtimeClass).asInstanceOf[T].value
  def get[T <: Param : ClassTag] = params.get(implicitly[ClassTag[T]].runtimeClass).map(_.asInstanceOf[T].value)
  def +[U >: T <: Param](param : U) = new ParamSet(params + ((param.getClass, param)))
  def ++[U >: T <: Param](params : TraversableOnce[U]) = new ParamSet(this.params ++ params.map(p => (p.getClass, p)))
  def iterator = params.valuesIterator
  def toAssoc = params.values.map(p => (p.name, p.stringValue))
  def toQuery = Uri.Query(toAssoc.toMap)
}

object ParamSet {
  def apply[T <: Param](params : T*) : ParamSet[T] = new ParamSet(params.map(p => (p.getClass, p)).toMap)
}
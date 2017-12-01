Enum Evolution
==============

.. contents::

In the continued development of a CorDapp an enumerated type that was fit for purpose at one time may
require changing. Normally, this would be problematic as anything serialised (and kept in a vault) would
run the risk of being unable to be deserialised in the future or older versions of the app still alive
within a compatibility zone my fail to deserialize a message.

To facilitate backward and forward support for alterations to enumerated types Corda's serialization
framework supports the evolution of such types through a well defined framework that allows different
versions to interoperate with serialised versions of an enumeration of differing versions.

This is achieved through the use of certain annotations. Whenever a change is made, an annotation
capturing the change must be added (whilst it can be ommited any interoperability will be lost). Corda
supports two modifications to enumerated types, adding new constants, and renaming exiting constants

.. warning:: Once added evolution annotations MUST NEVER be removed from a class, doing so will break
    both forward and backward compatability for this version of the class and any version moving
    forward

Renaming Consants
-----------------

Renmed constants are marked as such with the ``@CordaSerializationTransformRenames`` meta anotation that
wraps a list of ``@CordaSerializationTransformRename`` annotations. Each rename requiring an instance in the
list.

Each instance must provide the new name of the constant as well as the old. For example, consider the following enumeration

.. container:: codeset

   .. sourcecode:: kotlin

        enum class Example {
            A, B, C
        }

If we were to rename constant C to D this would be done thusly

.. container:: codeset

   .. sourcecode:: kotlin

        @CordaSerializationTransformRenames (
            CordaSerializationTransformRename(from = "C", to = "D")
        )
        enum class Example {
            A, B, D
        }

In the case where a single rename has been applied the meta annotation may be ommited. Thus, the following is
functionally identical to the above

.. container:: codeset

   .. sourcecode:: kotlin

        @CordaSerializationTransformRename(from = "C", to = "D")
        enum class Example {
            A, B, D
        }

However, as soon as a second rename is made the meta annotation must be used. For example, if at some time later
B is renamed to E

.. container:: codeset

   .. sourcecode:: kotlin

        @CordaSerializationTransformRenames (
            CordaSerializationTransformRename(from = "B", to = "E"),
            CordaSerializationTransformRename(from = "C", to = "D")
        )
        enum class Example {
            A, E, D
        }


Rules
~~~~~

    #.  A constant cannot be renamed to match an existin constant, this is enforced through language constraints
    #.  A constant cannot be renamed to a value that matchs any previous name of another constant

.. warning:: 



Examples
~~~~~~~~


Adding Constants
----------------

Rules
~~~~~

Examples
~~~~~~~~

Unsupported Evolutions
----------------------

    #.  Removing constants
    #.  Reordering constants
